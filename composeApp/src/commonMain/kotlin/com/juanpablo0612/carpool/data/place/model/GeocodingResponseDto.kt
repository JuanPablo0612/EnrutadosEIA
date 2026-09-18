package com.juanpablo0612.carpool.data.place.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResponseDto(
    val results: List<GeocodingResultDto> = emptyList(),
    val status: String = "",
)

@Serializable
data class GeocodingResultDto(
    @SerialName("formatted_address") val formattedAddress: String = "",
    @SerialName("place_id") val placeId: String = "",
    val geometry: GeometryDto? = null,
)

@Serializable
data class GeometryDto(val location: GeocodingLocationDto? = null)

@Serializable
data class GeocodingLocationDto(val lat: Double = 0.0, val lng: Double = 0.0)
