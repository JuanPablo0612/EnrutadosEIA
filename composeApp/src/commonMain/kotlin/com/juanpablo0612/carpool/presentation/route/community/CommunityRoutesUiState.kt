package com.juanpablo0612.carpool.presentation.route.community

import com.juanpablo0612.carpool.domain.route.model.Route

data class CommunityRoutesUiState(
    val routes: List<Route> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val reusingRouteId: String? = null,
    val error: CommunityRoutesError? = null,
    val actionError: CommunityRoutesError? = null
)
