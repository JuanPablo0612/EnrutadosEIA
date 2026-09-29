package com.juanpablo0612.carpool.presentation.auth.emailverification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.core.config.FeatureFlags
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.presentation.auth.AuthError
import com.juanpablo0612.carpool.presentation.auth.toAuthError
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmailVerificationViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailVerificationUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EmailVerificationEvent>()
    val events = _events.asSharedFlow()

    private var countdownJob: Job? = null
    private var checkJob: Job? = null

    /** Whether the check in flight should report its outcome; see [checkVerification]. */
    private var reportCheckResult = false

    init {
        loadUserEmail()
    }

    fun onAction(action: EmailVerificationAction) {
        when (action) {
            EmailVerificationAction.OnResendEmail -> resendEmail()
            EmailVerificationAction.OnCountdownTick -> {
                _uiState.update { it.copy(resendCountdown = (it.resendCountdown - 1).coerceAtLeast(0)) }
            }
            EmailVerificationAction.OnCheckVerification -> checkVerification(userInitiated = true)
            EmailVerificationAction.OnScreenResumed -> checkVerification(userInitiated = false)
            EmailVerificationAction.OnUseAnotherEmail -> useAnotherEmail()
        }
    }

    private fun loadUserEmail() {
        val email = authRepository.getCurrentUserEmail() ?: return
        val atIndex = email.indexOf('@')
        val obfuscated = if (atIndex > 1) email[0] + "***" + email.substring(atIndex) else email
        _uiState.update { it.copy(obfuscatedEmail = obfuscated) }
    }

    /**
     * Checks the auth token for a verified email. This replaces polling: the check runs when the
     * screen resumes (the user usually comes back from their mail) and when they tap the button,
     * and only refreshes Firebase Auth. The user's Firestore document is read once, on success.
     *
     * Only a check the user asked for reports back ("still unverified" or an error); a silent
     * check on resume stays quiet. A tap during a silent check joins it rather than being lost.
     */
    private fun checkVerification(userInitiated: Boolean) {
        if (userInitiated) {
            reportCheckResult = true
            _uiState.update { it.copy(isChecking = true, isStillUnverified = false, error = null) }
        }
        if (checkJob?.isActive == true) return
        checkJob = viewModelScope.launch {
            val verified = if (FeatureFlags.EMAIL_VERIFICATION_REQUIRED) {
                authRepository.refreshEmailVerification().getOrElse { throwable ->
                    finishCheck(error = throwable.toAuthError())
                    return@launch
                }
            } else {
                true
            }
            if (!verified) {
                finishCheck(stillUnverified = true)
                return@launch
            }
            authRepository.getCurrentUser()
                .onSuccess { user -> _events.emit(EmailVerificationEvent.NavigateToApp(user)) }
                .onFailure { throwable -> finishCheck(error = throwable.toAuthError()) }
        }
    }

    private fun finishCheck(stillUnverified: Boolean = false, error: AuthError? = null) {
        val report = reportCheckResult
        reportCheckResult = false
        _uiState.update {
            it.copy(
                isChecking = false,
                isStillUnverified = report && stillUnverified,
                error = if (report) error else it.error,
            )
        }
    }

    /**
     * Signs out of the unverified account before sending the user back to sign-up; otherwise
     * they would register the corrected address while still signed in to the old one.
     */
    private fun useAnotherEmail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.logout()
                .onSuccess { _events.emit(EmailVerificationEvent.NavigateToSignUp) }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.toAuthError()) }
                }
        }
    }

    private fun resendEmail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.sendEmailVerification()
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    startCountdown()
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.toAuthError()) }
                }
        }
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        _uiState.update { it.copy(resendCountdown = 30) }
        countdownJob = viewModelScope.launch {
            repeat(30) {
                delay(1000)
                onAction(EmailVerificationAction.OnCountdownTick)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
