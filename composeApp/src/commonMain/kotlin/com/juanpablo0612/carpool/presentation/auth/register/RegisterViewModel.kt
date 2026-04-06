package com.juanpablo0612.carpool.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.use_case.RegisterUseCase
import com.juanpablo0612.carpool.presentation.auth.common.AuthEvent
import com.juanpablo0612.carpool.presentation.auth.common.toAuthError
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events = _events.asSharedFlow()

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.OnFullNameChanged -> _uiState.update { it.copy(fullName = action.fullName) }
            is RegisterAction.OnEmailChanged -> _uiState.update { it.copy(email = action.email) }
            is RegisterAction.OnPasswordChanged -> _uiState.update { it.copy(password = action.password) }
            is RegisterAction.OnConfirmPasswordChanged -> _uiState.update { it.copy(confirmPassword = action.confirmPassword) }
            RegisterAction.OnTogglePasswordVisibility -> _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            RegisterAction.OnToggleConfirmPasswordVisibility -> _uiState.update {
                it.copy(
                    isConfirmPasswordVisible = !it.isConfirmPasswordVisible
                )
            }

            is RegisterAction.OnPassengerChanged -> _uiState.update { it.copy(isPassenger = action.isPassenger) }
            is RegisterAction.OnDriverChanged -> _uiState.update { it.copy(isDriver = action.isDriver) }
            RegisterAction.OnRegisterClicked -> register()
            RegisterAction.OnClearError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun register() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank() || state.fullName.isBlank()) return
        if (state.password != state.confirmPassword) {
            // TODO: Handle password mismatch error
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
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    _events.emit(AuthEvent.NavigateToHome)
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.toAuthError()) }
                }
        }
    }
}
