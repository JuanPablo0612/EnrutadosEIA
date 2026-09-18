package com.juanpablo0612.carpool.domain.place.model

data class AutocompleteSuggestion(
    val placeId: String,
    val primaryText: String,
    val secondaryText: String,
    val fullAddress: String,
)
