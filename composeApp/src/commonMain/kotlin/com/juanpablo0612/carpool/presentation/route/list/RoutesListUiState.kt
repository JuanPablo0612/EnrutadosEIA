package com.juanpablo0612.carpool.presentation.route.list

import com.juanpablo0612.carpool.domain.route.model.Route
import kotlinx.datetime.Instant

data class RouteWithStats(
    val route: Route,
    val tripsCount: Int = 0,
    val lastUsedAt: Instant? = null
)

data class RoutesListUiState(
    val routes: List<RouteWithStats> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val pendingDeleteRouteId: String? = null,
    val duplicatingRouteId: String? = null,
    val showDuplicateSuccess: Boolean = false,
    val error: RoutesListError? = null,
    val actionError: RoutesListError? = null
) {
    val filteredRoutes: List<RouteWithStats>
        get() = if (searchQuery.isBlank()) {
            routes
        } else {
            routes.filter {
                it.route.name.contains(searchQuery, ignoreCase = true) ||
                    it.route.origin.name.contains(searchQuery, ignoreCase = true) ||
                    it.route.destination.name.contains(searchQuery, ignoreCase = true)
            }
        }
}
