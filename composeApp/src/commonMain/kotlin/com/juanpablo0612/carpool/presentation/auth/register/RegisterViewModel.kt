package com.juanpablo0612.carpool.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.use_case.RegisterUseCase
import com.juanpablo0612.carpool.domain.auth.util.ValidationResult
import com.juanpablo0612.carpool.domain.auth.util.Validator
import com.juanpablo0612.carpool.presentation.auth.common.AuthEvent
import com.juanpablo0612.carpool.presentation.auth.common.toAuthError
import io.github.vinceglb.filekit.readBytes
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
            is RegisterAction.OnFullNameChanged -> _uiState.update { it.copy(fullName = action.fullName, fullNameError = null) }
            is RegisterAction.OnEmailChanged -> _uiState.update { it.copy(email = action.email, emailError = null) }
            is RegisterAction.OnPasswordChanged -> _uiState.update { it.copy(password = action.password, passwordError = null) }
            is RegisterAction.OnConfirmPasswordChanged -> _uiState.update { it.copy(confirmPassword = action.confirmPassword, confirmPasswordError = null) }
            RegisterAction.OnTogglePasswordVisibility -> _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            RegisterAction.OnToggleConfirmPasswordVisibility -> _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            is RegisterAction.OnPhotoSelected -> _uiState.update { it.copy(photoFile = action.file) }
            is RegisterAction.OnPhoneChanged -> _uiState.update { it.copy(phone = action.phone, phoneError = null) }
            is RegisterAction.OnPassengerChanged -> _uiState.update { it.copy(isPassenger = action.isPassenger, roleError = null) }
            is RegisterAction.OnDriverChanged -> _uiState.update { it.copy(isDriver = action.isDriver, roleError = null) }
            is RegisterAction.OnTermsChanged -> _uiState.update { it.copy(hasAcceptedTerms = action.accepted, termsError = false) }
            RegisterAction.OnNextStep -> advanceStep()
            RegisterAction.OnPreviousStep -> _uiState.update { if (it.currentStep > 1) it.copy(currentStep = it.currentStep - 1) else it }
            RegisterAction.OnRegisterClicked -> register()
        }
    }

    private fun advanceStep() {
        val state = _uiState.value
        when (state.currentStep) {
            1 -> {
                val nameResult = Validator.validateFullName(state.fullName)
                val emailResult = Validator.validateEmail(state.email)
                val passwordResult = Validator.validatePassword(state.password)
                val confirmResult = Validator.validateConfirmPassword(state.password, state.confirmPassword)

                val hasError = listOf(nameResult, emailResult, passwordResult, confirmResult).any { it is ValidationResult.Error }
                if (hasError) {
                    _uiState.update {
                        it.copy(
                            fullNameError = (nameResult as? ValidationResult.Error)?.error,
                            emailError = (emailResult as? ValidationResult.Error)?.error,
                            passwordError = (passwordResult as? ValidationResult.Error)?.error,
                            confirmPasswordError = (confirmResult as? ValidationResult.Error)?.error
                        )
                    }
                } else {
                    _uiState.update { it.copy(currentStep = 2) }
                }
            }
            2 -> {
                val phoneResult = Validator.validatePhone(state.phone)
                if (phoneResult is ValidationResult.Error) {
                    _uiState.update { it.copy(phoneError = phoneResult.error) }
                } else {
                    _uiState.update { it.copy(currentStep = 3) }
                }
            }
        }
    }

    private fun register() {
        val state = _uiState.value
        val roleResult = Validator.validateRole(state.isPassenger, state.isDriver)

        val roleError = (roleResult as? ValidationResult.Error)?.error
        val termsError = !state.hasAcceptedTerms

        if (roleError != null || termsError) {
            _uiState.update { it.copy(roleError = roleError, termsError = termsError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val photoBytes = state.photoFile?.readBytes()
            registerUseCase(
                email = state.email,
                password = state.password,
                name = state.fullName,
                isPassenger = state.isPassenger,
                isDriver = state.isDriver,
                phone = state.phone,
                photoBytes = photoBytes
            )
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AuthEvent.NavigateToEmailVerification)
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.toAuthError()) }
                }
        }
    }
}
