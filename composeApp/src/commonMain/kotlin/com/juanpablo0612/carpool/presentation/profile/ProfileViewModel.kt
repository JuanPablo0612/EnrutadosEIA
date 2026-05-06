package com.juanpablo0612.carpool.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.presentation.session.UserSession
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userSession: UserSession
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(userSession.user, userSession.activeRole) { user, role ->
                ProfileUiState(user = user, activeRole = role, isLoading = false)
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.OnLogoutClick -> viewModelScope.launch {
                _events.emit(ProfileEvent.LogoutSuccess)
            }
            ProfileAction.OnMyRoutesClick -> viewModelScope.launch {
                _events.emit(ProfileEvent.NavigateToRoutes)
            }
            ProfileAction.OnMyVehiclesClick -> viewModelScope.launch {
                _events.emit(ProfileEvent.NavigateToVehicles)
            }
        }
    }
}
