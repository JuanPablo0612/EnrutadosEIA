package com.juanpablo0612.carpool.presentation.routes.list

import com.juanpablo0612.carpool.domain.routes.model.Route
import kotlinx.datetime.Instant

data class RouteWithStats(
    val route: Route,
    val tripsCount: Int = 0,
    val lastUsedAt: Instant? = null
)

data class RoutesListUiState(
    val routes: List<RouteWithStats> = emptyList(),
    val isLoading: Boolean = true,
    val pendingDeleteRouteId: String? = null
)
