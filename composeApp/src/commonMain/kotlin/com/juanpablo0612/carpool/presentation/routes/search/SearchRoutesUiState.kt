package com.juanpablo0612.carpool.presentation.routes.search

import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.domain.routes.model.RouteType

data class SearchRoutesUiState(
    val routes: List<Route> = emptyList(),
    val isLoading: Boolean = true,
    val query: String = "",
    val selectedType: RouteType? = null
)
