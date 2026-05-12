package com.juanpablo0612.carpool.presentation.auth.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.use_case.SendPasswordResetEmailUseCase
import com.juanpablo0612.carpool.domain.auth.util.ValidationResult
import com.juanpablo0612.carpool.domain.auth.util.Validator
import com.juanpablo0612.carpool.presentation.auth.common.toAuthError
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ForgotPasswordEvent>()
    val events = _events.asSharedFlow()

    private var countdownJob: Job? = null

    fun onAction(action: ForgotPasswordAction) {
        when (action) {
            is ForgotPasswordAction.OnEmailChanged -> {
                _uiState.update { it.copy(email = action.email, emailError = null) }
            }
            ForgotPasswordAction.OnSendResetLink -> sendResetLink()
            ForgotPasswordAction.OnResendLink -> sendResetLink()
            ForgotPasswordAction.OnCountdownTick -> tick()
            ForgotPasswordAction.OnOpenGmail -> viewModelScope.launch {
                _events.emit(ForgotPasswordEvent.OpenGmail)
            }
        }
    }

    private fun sendResetLink() {
        val email = _uiState.value.email
        val emailResult = Validator.validateEmail(email)

        if (emailResult is ValidationResult.Error) {
            _uiState.update { it.copy(emailError = emailResult.error) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            sendPasswordResetEmailUseCase(email)
            // Always show success for privacy (don't reveal if email exists)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isSuccess = true,
                    obfuscatedEmail = obfuscateEmail(email)
                )
            }
            startCountdown()
        }
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
