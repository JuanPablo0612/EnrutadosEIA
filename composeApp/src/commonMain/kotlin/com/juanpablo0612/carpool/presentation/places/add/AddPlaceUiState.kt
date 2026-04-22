package com.juanpablo0612.carpool.presentation.places.add

data class AddPlaceUiState(
    val name: String = "",
    val address: String = "",
    val nameError: AddPlaceError? = null,
    val addressError: AddPlaceError? = null,
    val generalError: AddPlaceError? = null,
    val isLoading: Boolean = false
)
