package com.juanpablo0612.carpool.presentation.profile

sealed class ProfileEvent {
    data object LogoutSuccess : ProfileEvent()
    data object NavigateToRoutes : ProfileEvent()
    data object NavigateToVehicles : ProfileEvent()
}
