package com.juanpablo0612.carpool.presentation.route.search

import com.juanpablo0612.carpool.domain.trip.model.TripSearchCriteria

data class SearchFilters(
    val maxContribution: Int? = null,
    val maxWalkMeters: Int = TripSearchCriteria.DEFAULT_MAX_WALK_METERS,
)
