package com.juanpablo0612.carpool.data.place.model

import com.juanpablo0612.carpool.domain.place.model.AutocompleteSuggestion
import com.juanpablo0612.carpool.domain.place.model.Coordinates

fun PlacePredictionDto.toDomain(): AutocompleteSuggestion {
    val primary = structuredFormat?.mainText?.text ?: text?.text ?: ""
    val secondary = structuredFormat?.secondaryText?.text ?: ""
    val full = text?.text ?: listOfNotNull(
        structuredFormat?.mainText?.text,
        structuredFormat?.secondaryText?.text,
    ).joinToString(", ")
    return AutocompleteSuggestion(
        placeId = placeId,
        primaryText = primary,
        secondaryText = secondary,
        fullAddress = full,
    )
}

fun PlaceDetailsResponseDto.toCoordinates(): Coordinates? {
    val loc = location ?: return null
    return Coordinates(latitude = loc.latitude, longitude = loc.longitude)
}

fun GeocodingResultDto.toAddress(): String? = formattedAddress.takeIf { it.isNotBlank() }
