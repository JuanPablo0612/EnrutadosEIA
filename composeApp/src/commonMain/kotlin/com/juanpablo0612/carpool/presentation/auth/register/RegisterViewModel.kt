package com.juanpablo0612.carpool.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.use_case.GetCurrentUserUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.RegisterUseCase
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

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events = _events.asSharedFlow()

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.OnFullNameChanged -> {
                _uiState.update { it.copy(fullName = action.fullName, fullNameError = null) }
            }
            is RegisterAction.OnEmailChanged -> {
                _uiState.update { it.copy(email = action.email, emailError = null) }
            }
            is RegisterAction.OnPasswordChanged -> {
                _uiState.update { it.copy(password = action.password, passwordError = null) }
            }
            is RegisterAction.OnConfirmPasswordChanged -> {
                _uiState.update { it.copy(confirmPassword = action.confirmPassword, confirmPasswordError = null) }
            }
            RegisterAction.OnTogglePasswordVisibility -> _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            RegisterAction.OnToggleConfirmPasswordVisibility -> _uiState.update {
                it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible)
            }
            is RegisterAction.OnPassengerChanged -> _uiState.update { it.copy(isPassenger = action.isPassenger, roleError = null) }
            is RegisterAction.OnDriverChanged -> _uiState.update { it.copy(isDriver = action.isDriver, roleError = null) }
            RegisterAction.OnRegisterClicked -> register()
        }
    }

    private fun register() {
        val state = _uiState.value

        val nameResult = Validator.validateFullName(state.fullName)
        val emailResult = Validator.validateEmail(state.email)
        val passwordResult = Validator.validatePassword(state.password)
        val confirmResult = Validator.validateConfirmPassword(state.password, state.confirmPassword)
        val roleResult = Validator.validateRole(state.isPassenger, state.isDriver)

        val hasError = listOf(nameResult, emailResult, passwordResult, confirmResult, roleResult)
            .any { it is ValidationResult.Error }

        if (hasError) {
            _uiState.update {
                it.copy(
                    fullNameError = (nameResult as? ValidationResult.Error)?.error,
                    emailError = (emailResult as? ValidationResult.Error)?.error,
                    passwordError = (passwordResult as? ValidationResult.Error)?.error,
                    confirmPasswordError = (confirmResult as? ValidationResult.Error)?.error,
                    roleError = (roleResult as? ValidationResult.Error)?.error
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            registerUseCase(
                email = state.email,
                password = state.password,
                name = state.fullName,
                isPassenger = state.isPassenger,
                isDriver = state.isDriver
            )
                .onSuccess {
                    getCurrentUserUseCase()
                        .onSuccess { user ->
                            _uiState.update { it.copy(isLoading = false) }
                            _events.emit(AuthEvent.NavigateAfterAuth(user))
                        }
                        .onFailure { throwable ->
                            _uiState.update { it.copy(isLoading = false, error = throwable.toAuthError()) }
                        }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.toAuthError()) }
                }
        }
    }
}
