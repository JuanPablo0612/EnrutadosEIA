package com.juanpablo0612.carpool.presentation.auth.email_verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.use_case.GetCurrentUserUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.SendEmailVerificationUseCase
import com.juanpablo0612.carpool.presentation.auth.common.toAuthError
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmailVerificationViewModel(
    private val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailVerificationUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EmailVerificationEvent>()
    val events = _events.asSharedFlow()

    private var countdownJob: Job? = null
    private var pollingJob: Job? = null

    init {
        loadUserEmail()
        startPolling()
    }

    fun onAction(action: EmailVerificationAction) {
        when (action) {
            EmailVerificationAction.OnResendEmail -> resendEmail()
            EmailVerificationAction.OnCountdownTick -> {
                _uiState.update { it.copy(resendCountdown = (it.resendCountdown - 1).coerceAtLeast(0)) }
            }
            EmailVerificationAction.OnOpenGmail -> viewModelScope.launch {
                _events.emit(EmailVerificationEvent.OpenGmail)
            }
        }
    }

    private fun loadUserEmail() {
        viewModelScope.launch {
            getCurrentUserUseCase().onSuccess { user ->
                val email = user.email
                val atIndex = email.indexOf('@')
                val obfuscated = if (atIndex > 1) email[0] + "***" + email.substring(atIndex) else email
                _uiState.update { it.copy(obfuscatedEmail = obfuscated) }
            }
        }
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(5000)
                getCurrentUserUseCase().onSuccess { user ->
                    if (user.isEmailVerified) {
                        pollingJob?.cancel()
                        _events.emit(EmailVerificationEvent.NavigateToApp(user))
                    }
                }
            }
        }
    }

    private fun resendEmail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            sendEmailVerificationUseCase()
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
        pollingJob?.cancel()
        countdownJob?.cancel()
    }
}
