package com.juanpablo0612.carpool.presentation.auth.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.auth.validation.EiaEmail
import com.juanpablo0612.carpool.domain.auth.validation.ValidationResult
import com.juanpablo0612.carpool.domain.auth.validation.Validator
import com.juanpablo0612.carpool.presentation.auth.AuthError
import com.juanpablo0612.carpool.presentation.auth.toAuthError
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    prefilledEmail: String?,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ForgotPasswordUiState(email = prefilledEmail?.let(EiaEmail::toInput).orEmpty())
    )
    val uiState = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    fun onAction(action: ForgotPasswordAction) {
        when (action) {
            is ForgotPasswordAction.OnEmailChanged -> {
                _uiState.update { it.copy(email = action.email, emailError = null) }
            }
            ForgotPasswordAction.OnSendResetLink -> sendResetLink()
            ForgotPasswordAction.OnResendLink -> sendResetLink()
            ForgotPasswordAction.OnCountdownTick -> tick()
        }
    }

    private fun sendResetLink() {
        val email = EiaEmail.fromInput(_uiState.value.email)
        val emailResult = Validator.validateEmail(email)

        if (emailResult is ValidationResult.Error) {
            _uiState.update { it.copy(emailError = emailResult.error) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.sendPasswordResetEmail(email).fold(
                onSuccess = { onResetLinkSucceeded(email) },
                onFailure = { throwable ->
                    val authError = throwable.toAuthError()
                    if (authError is AuthError.UserNotFound) {
                        // Always show success for privacy (don't reveal if the email exists).
                        onResetLinkSucceeded(email)
                    } else {
                        // A genuine failure (network, unknown) — unlike UserNotFound, this
                        // doesn't reveal anything about the account, so it's safe to surface.
                        _uiState.update { it.copy(isLoading = false, error = authError) }
                    }
                }
            )
        }
    }

    private fun onResetLinkSucceeded(email: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                isSuccess = true,
                obfuscatedEmail = obfuscateEmail(email)
            )
        }
        startCountdown()
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        _uiState.update { it.copy(resendCountdown = 30) }
        countdownJob = viewModelScope.launch {
            repeat(30) {
                delay(1000)
                onAction(ForgotPasswordAction.OnCountdownTick)
            }
        }
    }

    private fun tick() {
        _uiState.update { it.copy(resendCountdown = (it.resendCountdown - 1).coerceAtLeast(0)) }
    }

    private fun obfuscateEmail(email: String): String {
        val atIndex = email.indexOf('@')
        if (atIndex <= 1) return email
        return email[0] + "***" + email.substring(atIndex)
    }
}
