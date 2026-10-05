package com.juanpablo0612.carpool.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.core.config.FeatureFlags
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.auth.validation.EiaEmail
import com.juanpablo0612.carpool.domain.auth.validation.PasswordStrength
import com.juanpablo0612.carpool.domain.auth.validation.ValidationResult
import com.juanpablo0612.carpool.domain.auth.validation.Validator
import com.juanpablo0612.carpool.presentation.auth.AuthEvent
import com.juanpablo0612.carpool.presentation.auth.toAuthError
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events = _events.asSharedFlow()

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.OnFullNameChanged -> _uiState.update { it.copy(fullName = action.fullName, fullNameError = null) }
            is RegisterAction.OnEmailChanged -> _uiState.update { it.copy(email = action.email, emailError = null) }
            is RegisterAction.OnPasswordChanged -> _uiState.update {
                it.copy(
                    password = action.password,
                    passwordError = null,
                    passwordStrength = action.password.takeIf { p -> p.isNotEmpty() }?.let(PasswordStrength::of),
                )
            }
            is RegisterAction.OnConfirmPasswordChanged -> _uiState.update { it.copy(confirmPassword = action.confirmPassword, confirmPasswordError = null) }
            RegisterAction.OnTogglePasswordVisibility -> _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            RegisterAction.OnToggleConfirmPasswordVisibility -> _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            is RegisterAction.OnPhotoSelected -> _uiState.update { it.copy(photoFile = action.file, photoError = false) }
            is RegisterAction.OnPhoneChanged -> _uiState.update { it.copy(phone = action.phone, phoneError = null) }
            is RegisterAction.OnTermsChanged -> _uiState.update { it.copy(hasAcceptedTerms = action.accepted, termsError = false) }
            RegisterAction.OnNextStep -> advanceStep()
            RegisterAction.OnPreviousStep -> _uiState.update { if (it.currentStep > 1) it.copy(currentStep = it.currentStep - 1) else it }
            RegisterAction.OnRegisterClicked -> register()
        }
    }

    /** Validates the account step and moves on to the profile step. */
    private fun advanceStep() {
        val state = _uiState.value
        if (state.currentStep != 1) return

        val nameResult = Validator.validateFullName(state.fullName)
        val emailResult = Validator.validateEmail(EiaEmail.fromInput(state.email))
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

    private fun register() {
        val state = _uiState.value
        val phoneError = (Validator.validatePhone(state.phone) as? ValidationResult.Error)?.error
        val termsError = !state.hasAcceptedTerms

        if (phoneError != null || termsError) {
            _uiState.update { it.copy(phoneError = phoneError, termsError = termsError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, photoError = false) }
            val photoBytes = try {
                state.photoFile?.readBytes()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, photoError = true) }
                return@launch
            }
            authRepository.register(
                email = EiaEmail.fromInput(state.email),
                password = state.password,
                name = state.fullName,
                phone = Validator.normalizePhone(state.phone),
                photoBytes = photoBytes
            )
                .onSuccess {
                    if (FeatureFlags.EMAIL_VERIFICATION_REQUIRED) {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.emit(AuthEvent.NavigateToEmailVerification)
                    } else {
                        navigateAfterAuth()
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.toAuthError()) }
                }
        }
    }

    private suspend fun navigateAfterAuth() {
        authRepository.getCurrentUser()
            .onSuccess { user ->
                _uiState.update { it.copy(isLoading = false) }
                _events.emit(AuthEvent.NavigateAfterAuth(user))
            }
            .onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false, error = throwable.toAuthError()) }
            }
    }
}
