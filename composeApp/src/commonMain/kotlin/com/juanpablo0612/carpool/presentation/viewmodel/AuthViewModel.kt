package com.juanpablo0612.carpool.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.core.AppResult
import com.juanpablo0612.carpool.domain.usecase.LoginUseCase
import com.juanpablo0612.carpool.domain.usecase.LogoutUseCase
import com.juanpablo0612.carpool.domain.usecase.ObserveAuthStateUseCase
import com.juanpablo0612.carpool.domain.usecase.RegisterUseCase
import com.juanpablo0612.carpool.presentation.state.AuthAction
import com.juanpablo0612.carpool.presentation.state.AuthEvent
import com.juanpablo0612.carpool.presentation.state.AuthUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events = _events.asSharedFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        observeAuthStateUseCase()
            .onEach { user ->
                _uiState.update { it.copy(user = user, isLoggedIn = user != null) }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.Login -> login(action.email, action.password)
            is AuthAction.Register -> register(action.email, action.password)
            AuthAction.Logout -> logout()
            AuthAction.ClearError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun login(email: String, password: String) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = loginUseCase(email, password)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AuthEvent.NavigateToHome)
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.error) }
                }
            }
        }
    }

    private fun register(email: String, password: String) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = registerUseCase(email, password)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AuthEvent.NavigateToHome)
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.error) }
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _events.emit(AuthEvent.NavigateToLogin)
        }
    }
}
