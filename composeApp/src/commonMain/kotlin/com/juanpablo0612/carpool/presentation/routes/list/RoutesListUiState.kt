package com.juanpablo0612.carpool.presentation.routes.list

import com.juanpablo0612.carpool.domain.routes.model.Route

data class RoutesListUiState(
    val routes: List<Route> = emptyList(),
    val isLoading: Boolean = true
)
