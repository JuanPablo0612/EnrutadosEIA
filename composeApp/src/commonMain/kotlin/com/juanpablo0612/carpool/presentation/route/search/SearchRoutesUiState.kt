package com.juanpablo0612.carpool.presentation.route.search

import com.juanpablo0612.carpool.domain.place.model.Place

data class SearchRoutesUiState(
    val results: List<TripResult> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val origin: Place? = null,
    val destination: Place? = Place.UNIVERSITY_EIA,
    val selectedEpochMs: Long? = null,
    val toleranceMinutes: Int = 30,
    val filters: SearchFilters = SearchFilters(),
    val showFiltersSheet: Boolean = false,
    val showDateTimeSheet: Boolean = false,
    val hasSearched: Boolean = false,
    val selectionTarget: String? = null  // "ORIGIN" | "DESTINATION" | null
)
