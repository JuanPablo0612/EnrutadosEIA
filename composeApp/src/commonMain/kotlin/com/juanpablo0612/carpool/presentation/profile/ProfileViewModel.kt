package com.juanpablo0612.carpool.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.payment.repository.PaymentMethodRepository
import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository
import com.juanpablo0612.carpool.presentation.auth.toAuthError
import com.juanpablo0612.carpool.presentation.session.UserSession
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userSession: UserSession,
    private val authRepository: AuthRepository,
    private val vehicleRepository: VehicleRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            userSession.user.collect { user ->
                _state.update { it.copy(user = user, isLoading = false) }
            }
        }
        observeSummaries()
    }

    // Both are live listeners, so the cards stay current when the user returns from editing.
    private fun observeSummaries() {
        val userId = authRepository.getCurrentUserId() ?: return
        vehicleRepository.getUserVehicles(userId)
            .onEach { vehicles -> _state.update { it.copy(vehicleSummary = vehicles.toVehicleSummary()) } }
            .catch { _state.update { it.copy(vehicleSummary = null) } }
            .launchIn(viewModelScope)
        paymentMethodRepository.observePaymentMethods(userId)
            .onEach { methods -> _state.update { it.copy(paymentSummary = methods.toPaymentSummary()) } }
            .catch { _state.update { it.copy(paymentSummary = null) } }
            .launchIn(viewModelScope)
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
            ProfileAction.OnPaymentMethodsClick -> viewModelScope.launch {
                _events.emit(ProfileEvent.NavigateToPaymentMethods)
            }

            ProfileAction.OnDeleteAccountClick -> _state.update {
                it.copy(showDeleteAccountDialog = true, deleteAccountNameInput = "", deleteAccountError = null)
            }
            ProfileAction.OnDeleteAccountDismissed -> _state.update {
                it.copy(showDeleteAccountDialog = false, deleteAccountNameInput = "", deleteAccountError = null)
            }
            is ProfileAction.OnDeleteAccountNameChange -> _state.update {
                it.copy(deleteAccountNameInput = action.name)
            }
            ProfileAction.OnDeleteAccountConfirmed -> deleteAccount()
        }
    }

    private fun deleteAccount() {
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true, deleteAccountError = null) }
            authRepository.deleteAccount().fold(
                onSuccess = {
                    _state.update { it.copy(isDeleting = false, showDeleteAccountDialog = false) }
                    userSession.clearSession()
                    _events.emit(ProfileEvent.DeleteAccountSuccess)
                },
                onFailure = { error ->
                    _state.update { it.copy(isDeleting = false, deleteAccountError = error.toAuthError()) }
                }
            )
        }
    }
}
