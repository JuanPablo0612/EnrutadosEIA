package com.juanpablo0612.carpool.presentation.route.search

import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.SearchRelaxation
import com.juanpablo0612.carpool.presentation.trip.TripError

data class SearchRoutesUiState(
    val results: List<TripResult> = emptyList(),
    val isLoading: Boolean = true,
    val loadError: TripError? = null,
    val isSearching: Boolean = false,
    val isRefreshing: Boolean = false,
    val origin: Place? = null,
    val destination: Place? = Place.UNIVERSITY_EIA,
    val selectedEpochMs: Long? = null,
    val toleranceMinutes: Int = 30,
    val filters: SearchFilters = SearchFilters(),
    val showFiltersSheet: Boolean = false,
    val showDateTimeSheet: Boolean = false,
    val hasSearched: Boolean = false,
    val relaxation: SearchRelaxation? = null,
    val selectionTarget: SearchPlaceTarget? = null,
)

/** Which search field the inline place selector is currently filling. */
sealed class SearchPlaceTarget {
    data object Origin : SearchPlaceTarget()
    data object Destination : SearchPlaceTarget()
}
