package com.juanpablo0612.carpool.domain.routes.model

sealed class RouteError {
    data object EmptyOrigin : RouteError()
    data object EmptyDestination : RouteError()
    data object RouteNotFound : RouteError()
    data object Unauthorized : RouteError()
    data object UnknownError : RouteError()
}
