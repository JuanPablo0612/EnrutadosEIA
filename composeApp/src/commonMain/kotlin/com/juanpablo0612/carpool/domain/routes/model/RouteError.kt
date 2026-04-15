package com.juanpablo0612.carpool.domain.routes.model

sealed class RouteError {
    data object EmptyOrigin : RouteError()
    data object EmptyDestination : RouteError()
    data object NoWaypoints : RouteError()
    data object NoDaysSelected : RouteError()
    data object InvalidTime : RouteError()
    data object RouteNotFound : RouteError()
    data object Unauthorized : RouteError()
    data object UnknownError : RouteError()
}
