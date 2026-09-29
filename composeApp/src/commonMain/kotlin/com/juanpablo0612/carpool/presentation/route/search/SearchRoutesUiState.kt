package com.juanpablo0612.carpool.presentation.route.search

import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.CampusDirection
import com.juanpablo0612.carpool.domain.trip.model.SearchRelaxation
import com.juanpablo0612.carpool.presentation.trip.TripError

data class SearchRoutesUiState(
    val results: List<TripResult> = emptyList(),
    val isLoading: Boolean = true,
    val loadError: TripError? = null,
    val direction: CampusDirection = CampusDirection.ToCampus,
    val campus: Place = Place.EIA_LAS_PALMAS,
    /** The passenger's end of the trip; null searches every trip for [campus] and [direction]. */
    val place: Place? = null,
    val selectedEpochMs: Long? = null,
    val toleranceMinutes: Int = 30,
    val showDateTimeSheet: Boolean = false,
    val hasSearched: Boolean = false,
    val relaxation: SearchRelaxation? = null,
    /** The place selector is covering the search while the passenger picks [place]. */
    val isPickingPlace: Boolean = false,
)
