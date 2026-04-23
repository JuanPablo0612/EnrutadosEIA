package com.juanpablo0612.carpool.presentation.routes.search

import com.juanpablo0612.carpool.domain.routes.model.RouteType

sealed class SearchRoutesAction {
    data class OnQueryChanged(val query: String) : SearchRoutesAction()
    data class OnTypeFilterChanged(val type: RouteType?) : SearchRoutesAction()
}
