package com.juanpablo0612.carpool.presentation.routes.search

import com.juanpablo0612.carpool.domain.trip.model.Trip

data class SearchRoutesUiState(
    val trips: List<Trip> = emptyList(),
    val isLoading: Boolean = true,
    val query: String = ""
)
