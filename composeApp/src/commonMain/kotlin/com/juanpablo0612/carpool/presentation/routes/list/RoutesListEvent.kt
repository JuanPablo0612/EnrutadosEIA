package com.juanpablo0612.carpool.presentation.routes.list

sealed class RoutesListEvent {
    data object NavigateToCreateRoute : RoutesListEvent()
}
