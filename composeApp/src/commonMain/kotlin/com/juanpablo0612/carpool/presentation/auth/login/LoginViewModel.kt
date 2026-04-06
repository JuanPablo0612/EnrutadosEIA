package com.juanpablo0612.carpool.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.use_case.LoginUseCase
import com.juanpablo0612.carpool.domain.auth.util.ValidationError
import com.juanpablo0612.carpool.domain.auth.util.ValidationResult
import com.juanpablo0612.carpool.domain.auth.util.Validator
import com.juanpablo0612.carpool.presentation.auth.common.AuthEvent
import com.juanpablo0612.carpool.presentation.auth.common.toAuthError
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events = _events.asSharedFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnEmailChanged -> _uiState.update { it.copy(email = action.email, emailError = null) }
            is LoginAction.OnPasswordChanged -> _uiState.update { it.copy(password = action.password, passwordError = null) }
            LoginAction.OnTogglePasswordVisibility -> _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            LoginAction.OnLoginClicked -> login()
            LoginAction.OnClearError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun login() {
        val state = _uiState.value
        
        val emailResult = Validator.validateEmail(state.email)
        val passwordResult = Validator.validatePassword(state.password)

        if (emailResult is ValidationResult.Error || passwordResult is ValidationResult.Error) {
            _uiState.update { 
                it.copy(
                    emailError = (emailResult as? ValidationResult.Error)?.error,
                    passwordError = (passwordResult as? ValidationResult.Error)?.error
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            loginUseCase(state.email, state.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    _events.emit(AuthEvent.NavigateToHome)
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.toAuthError()) }
                }
        }
    }
}
