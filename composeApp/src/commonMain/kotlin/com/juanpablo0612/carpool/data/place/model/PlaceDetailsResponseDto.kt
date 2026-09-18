package com.juanpablo0612.carpool.data.place.model

import kotlinx.serialization.Serializable

@Serializable
data class PlaceDetailsResponseDto(
    val formattedAddress: String = "",
    val location: LatLngDto? = null,
)
