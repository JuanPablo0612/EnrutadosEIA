package com.juanpablo0612.carpool.domain.places.model

sealed class PlaceType {
    data object Home : PlaceType()
    data object Work : PlaceType()
    data object Gym : PlaceType()
    data object University : PlaceType()
    data object Other : PlaceType()
}
