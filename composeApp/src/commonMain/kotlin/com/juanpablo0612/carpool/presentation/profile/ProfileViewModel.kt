package com.juanpablo0612.carpool.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.model.UserRole
import com.juanpablo0612.carpool.domain.auth.use_case.DeleteAccountUseCase
import com.juanpablo0612.carpool.domain.auth.use_case.UpdateUserRolesUseCase
import com.juanpablo0612.carpool.presentation.session.UserSession
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userSession: UserSession,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val updateUserRolesUseCase: UpdateUserRolesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(userSession.user, userSession.activeRole) { user, role ->
                user to role
            }.collect { (user, role) ->
                _state.update { it.copy(user = user, activeRole = role, isLoading = false) }
            }
        }
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.OnLogoutClick -> _state.update { it.copy(showLogoutDialog = true) }
            ProfileAction.OnLogoutConfirmed -> {
                _state.update { it.copy(showLogoutDialog = false) }
                viewModelScope.launch { _events.emit(ProfileEvent.LogoutSuccess) }
            }
            ProfileAction.OnLogoutDismissed -> _state.update { it.copy(showLogoutDialog = false) }

            ProfileAction.OnMyRoutesClick -> viewModelScope.launch {
                _events.emit(ProfileEvent.NavigateToRoutes)
            }
            ProfileAction.OnMyVehiclesClick -> viewModelScope.launch {
                _events.emit(ProfileEvent.NavigateToVehicles)
            }
            ProfileAction.OnEditProfileClick -> viewModelScope.launch {
                _events.emit(ProfileEvent.NavigateToEditProfile)
            }
            ProfileAction.OnSavedPlacesClick -> viewModelScope.launch {
                _events.emit(ProfileEvent.NavigateToSavedPlaces)
            }
            ProfileAction.OnNotificationsClick -> viewModelScope.launch {
                _events.emit(ProfileEvent.NavigateToNotifications)
            }
            ProfileAction.OnSafetyClick -> viewModelScope.launch {
                _events.emit(ProfileEvent.NavigateToSafety)
            }

            ProfileAction.OnActiveRolesClick -> _state.update { it.copy(showActiveRolesDialog = true) }
            ProfileAction.OnActiveRolesDismissed -> _state.update {
                it.copy(showActiveRolesDialog = false, blockedRoleToggleMessage = null)
            }
            is ProfileAction.OnToggleRole -> handleRoleToggle(action.role, action.enabled)

            ProfileAction.OnDeleteAccountClick -> _state.update {
                it.copy(showDeleteAccountDialog = true, deleteAccountNameInput = "")
            }
            ProfileAction.OnDeleteAccountDismissed -> _state.update {
                it.copy(showDeleteAccountDialog = false, deleteAccountNameInput = "")
            }
            is ProfileAction.OnDeleteAccountNameChange -> _state.update {
                it.copy(deleteAccountNameInput = action.name)
            }
            ProfileAction.OnDeleteAccountConfirmed -> deleteAccount()
        }
    }

    private fun handleRoleToggle(role: UserRole, enabled: Boolean) {
        val user = _state.value.user ?: return
        val newIsDriver = if (role == UserRole.Driver) enabled else user.isDriver
        val newIsPassenger = if (role == UserRole.Passenger) enabled else user.isPassenger
        if (!newIsDriver && !newIsPassenger) {
            _state.update { it.copy(blockedRoleToggleMessage = "Debes tener al menos un rol activo.") }
            return
        }
        _state.update { it.copy(blockedRoleToggleMessage = null) }
        viewModelScope.launch {
            updateUserRolesUseCase(newIsDriver, newIsPassenger).onSuccess { updatedUser ->
                userSession.setUser(updatedUser)
            }
        }
    }

    private fun deleteAccount() {
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            deleteAccountUseCase().fold(
                onSuccess = {
                    _state.update { it.copy(isDeleting = false, showDeleteAccountDialog = false) }
                    userSession.clearSession()
                    _events.emit(ProfileEvent.DeleteAccountSuccess)
                },
                onFailure = {
                    _state.update { it.copy(isDeleting = false) }
                }
            )
        }
    }
}
