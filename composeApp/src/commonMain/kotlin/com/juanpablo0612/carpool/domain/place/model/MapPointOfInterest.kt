package com.juanpablo0612.carpool.domain.place.model

data class MapPointOfInterest(
    val placeId: String,
    val name: String,
    val coordinates: Coordinates,
)
