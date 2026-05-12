package com.juanpablo0612.carpool.presentation.role_selector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.model.UserRole
import com.juanpablo0612.carpool.domain.booking.use_case.GetDriverBookingRequestsUseCase
import com.juanpablo0612.carpool.domain.preferences.use_case.GetRolePreferenceUseCase
import com.juanpablo0612.carpool.domain.preferences.use_case.SaveRolePreferenceUseCase
import com.juanpablo0612.carpool.presentation.session.UserSession
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RoleSelectorViewModel(
    private val userSession: UserSession,
    private val getDriverBookingRequestsUseCase: GetDriverBookingRequestsUseCase,
    private val getRolePreferenceUseCase: GetRolePreferenceUseCase,
    private val saveRolePreferenceUseCase: SaveRolePreferenceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoleSelectorUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<RoleSelectorEvent>()
    val events = _events.asSharedFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val user = userSession.user.value
            _uiState.update { it.copy(userName = user?.name?.split(" ")?.firstOrNull() ?: "") }

            val userId = user?.id ?: return@launch
            val bookings = getDriverBookingRequestsUseCase(userId).first()
            _uiState.update { it.copy(driverPendingCount = bookings.size) }
        }
    }

    fun onAction(action: RoleSelectorAction) {
        when (action) {
            RoleSelectorAction.OnSelectDriver -> selectRole(UserRole.Driver)
            RoleSelectorAction.OnSelectPassenger -> selectRole(UserRole.Passenger)
            is RoleSelectorAction.OnToggleRememberChoice -> _uiState.update { it.copy(rememberChoice = action.checked) }
        }
    }

    private fun selectRole(role: UserRole) {
        viewModelScope.launch {
            if (_uiState.value.rememberChoice) {
                saveRolePreferenceUseCase(role)
            }
            val event = when (role) {
                UserRole.Driver -> RoleSelectorEvent.NavigateToDriver
                UserRole.Passenger -> RoleSelectorEvent.NavigateToPassenger
            }
            _events.emit(event)
        }
    }
}
