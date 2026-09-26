package com.juanpablo0612.carpool.presentation.profile


sealed class ProfileAction {
    data object OnLogoutClick : ProfileAction()
    data object OnLogoutConfirmed : ProfileAction()
    data object OnLogoutDismissed : ProfileAction()
    data object OnMyRoutesClick : ProfileAction()
    data object OnMyVehiclesClick : ProfileAction()
    data object OnEditProfileClick : ProfileAction()
    data object OnSavedPlacesClick : ProfileAction()
    data object OnNotificationsClick : ProfileAction()
    data object OnDeleteAccountClick : ProfileAction()
    data object OnDeleteAccountDismissed : ProfileAction()
    data class OnDeleteAccountNameChange(val name: String) : ProfileAction()
    data object OnDeleteAccountConfirmed : ProfileAction()
}
