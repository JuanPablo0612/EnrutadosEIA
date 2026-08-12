package com.juanpablo0612.carpool.presentation.profile

import com.juanpablo0612.carpool.domain.auth.model.UserRole

sealed class ProfileEvent {
    data object LogoutSuccess : ProfileEvent()
    data object NavigateToRoutes : ProfileEvent()
    data object NavigateToVehicles : ProfileEvent()
    data object NavigateToEditProfile : ProfileEvent()
    data object NavigateToSavedPlaces : ProfileEvent()
    data object NavigateToNotifications : ProfileEvent()
    data object NavigateToSafety : ProfileEvent()
    data object DeleteAccountSuccess : ProfileEvent()

    /** The active role was disabled, so [role] — the one that's still on — takes over (3.9). */
    data class RoleSwitched(val role: UserRole) : ProfileEvent()
}
