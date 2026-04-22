package com.juanpablo0612.carpool.presentation.places.add

sealed class AddPlaceAction {
    data class OnNameChanged(val name: String) : AddPlaceAction()
    data class OnAddressChanged(val address: String) : AddPlaceAction()
    data object OnSaveClick : AddPlaceAction()
    data object OnBackClick : AddPlaceAction()
}
