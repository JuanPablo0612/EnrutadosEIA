package com.juanpablo0612.carpool.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.core.config.FeatureFlags
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.preferences.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SplashEvent>(replay = 1)
    val events = _events.asSharedFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            if (!userPreferencesRepository.hasSeenOnboarding()) {
                _events.emit(SplashEvent.NavigateToOnboarding)
                return@launch
            }
            if (authRepository.getCurrentUserId() == null) {
                _events.emit(SplashEvent.NavigateToAuth)
                return@launch
            }
            authRepository.getCurrentUser()
                .onSuccess { user -> _events.emit(user.toSplashEvent()) }
                .onFailure { _events.emit(SplashEvent.NavigateToAuth) }
        }
    }

    private fun User.toSplashEvent(): SplashEvent =
        if (FeatureFlags.EMAIL_VERIFICATION_REQUIRED && !isEmailVerified) {
            SplashEvent.NavigateToEmailVerification
        } else {
            SplashEvent.NavigateToHome(this)
        }
}
