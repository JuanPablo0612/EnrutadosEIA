package com.juanpablo0612.carpool.data.place.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AutocompleteRequestDto(
    val input: String,
    @SerialName("locationBias") val locationBias: LocationBiasDto? = null,
    @SerialName("languageCode") val languageCode: String = "es",
    @SerialName("includedRegionCodes") val includedRegionCodes: List<String> = emptyList(),
    @SerialName("sessionToken") val sessionToken: String? = null,
)

@Serializable
data class LocationBiasDto(val circle: CircleDto? = null)

@Serializable
data class CircleDto(val center: LatLngDto, val radius: Double = 50_000.0)

@Serializable
data class LatLngDto(val latitude: Double = 0.0, val longitude: Double = 0.0)

@Serializable
data class AutocompleteResponseDto(val suggestions: List<SuggestionDto> = emptyList())

@Serializable
data class SuggestionDto(val placePrediction: PlacePredictionDto? = null)

@Serializable
data class PlacePredictionDto(
    @SerialName("placeId") val placeId: String = "",
    val text: TextDto? = null,
    val structuredFormat: StructuredFormatDto? = null,
)

@Serializable
data class TextDto(val text: String = "")

@Serializable
data class StructuredFormatDto(
    val mainText: TextDto? = null,
    val secondaryText: TextDto? = null,
)
