package com.juanpablo0612.carpool.presentation.routes.detail

import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.presentation.routes.create.CreateRouteUiState
import kotlinx.datetime.Instant

data class RouteDetailUiState(
    val isLoading: Boolean = true,
    val route: Route? = null,
    val tripsPublished: Int = 0,
    val lastUsedAt: Instant? = null,
    val isEditing: Boolean = false,
    val draft: CreateRouteUiState? = null,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val showDeleteConfirm: Boolean = false,
    val error: String? = null
)
