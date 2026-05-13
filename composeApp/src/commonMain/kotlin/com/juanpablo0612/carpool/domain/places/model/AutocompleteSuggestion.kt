package com.juanpablo0612.carpool.domain.places.model

data class AutocompleteSuggestion(
    val placeId: String,
    val primaryText: String,
    val secondaryText: String,
    val fullAddress: String,
)
