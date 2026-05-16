package com.juanpablo0612.carpool.presentation.profile

sealed class ProfileEvent {
    data object LogoutSuccess : ProfileEvent()
    data object NavigateToRoutes : ProfileEvent()
    data object NavigateToVehicles : ProfileEvent()
    data object NavigateToEditProfile : ProfileEvent()
    data object NavigateToSavedPlaces : ProfileEvent()
    data object NavigateToNotifications : ProfileEvent()
    data object NavigateToSafety : ProfileEvent()
    data object DeleteAccountSuccess : ProfileEvent()
}
