package com.juanpablo0612.carpool.presentation.profile

sealed class ProfileAction {
    data object OnLogoutClick : ProfileAction()
    data object OnLogoutConfirmed : ProfileAction()
    data object OnLogoutDismissed : ProfileAction()
    data object OnMyRoutesClick : ProfileAction()
    data object OnMyVehiclesClick : ProfileAction()
}
