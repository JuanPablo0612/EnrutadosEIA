package com.juanpablo0612.carpool.domain.places.model

data class AutocompleteSuggestion(
    val latitude: Double,
    val longitude: Double,
    val primaryText: String,
    val secondaryText: String,
    val fullAddress: String,
)
