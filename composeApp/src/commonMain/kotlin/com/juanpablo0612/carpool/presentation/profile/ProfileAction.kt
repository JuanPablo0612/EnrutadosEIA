package com.juanpablo0612.carpool.presentation.profile

sealed class ProfileAction {
    data object OnLogoutClick : ProfileAction()
    data object OnMyRoutesClick : ProfileAction()
    data object OnMyVehiclesClick : ProfileAction()
}
