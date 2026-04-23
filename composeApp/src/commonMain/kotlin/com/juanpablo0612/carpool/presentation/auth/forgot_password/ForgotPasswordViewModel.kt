package com.juanpablo0612.carpool.presentation.auth.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.use_case.SendPasswordResetEmailUseCase
import com.juanpablo0612.carpool.domain.auth.util.ValidationResult
import com.juanpablo0612.carpool.domain.auth.util.Validator
import com.juanpablo0612.carpool.presentation.auth.common.toAuthError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: ForgotPasswordAction) {
        when (action) {
            is ForgotPasswordAction.OnEmailChanged -> {
                _uiState.update { it.copy(email = action.email, emailError = null) }
            }
            ForgotPasswordAction.OnSendResetLink -> sendResetLink()
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
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.toAuthError()
                        )
                    }
                }
        }
    }
}
