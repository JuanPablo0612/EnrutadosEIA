package com.juanpablo0612.carpool.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.domain.auth.model.UserRole
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.auth.use_case.GetCurrentUserUseCase
import com.juanpablo0612.carpool.domain.preferences.use_case.GetRolePreferenceUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val authRepository: AuthRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getRolePreferenceUseCase: GetRolePreferenceUseCase
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
            if (authRepository.getCurrentUserId() == null) {
                _events.emit(SplashEvent.NavigateToAuth)
                return@launch
            }
            getCurrentUserUseCase()
                .onSuccess { user -> _events.emit(user.toSplashEvent()) }
                .onFailure { _events.emit(SplashEvent.NavigateToAuth) }
        }
    }

    private suspend fun User.toSplashEvent(): SplashEvent = when {
        isDriver && isPassenger -> {
            val savedRole = getRolePreferenceUseCase()
            when (savedRole) {
                UserRole.Driver -> SplashEvent.NavigateToDriver(this)
                UserRole.Passenger -> SplashEvent.NavigateToPassenger(this)
                null -> SplashEvent.NavigateToRoleSelector(this)
            }
        }
        isDriver -> SplashEvent.NavigateToDriver(this)
        isPassenger -> SplashEvent.NavigateToPassenger(this)
        else -> SplashEvent.NavigateToAuth
    }
}
