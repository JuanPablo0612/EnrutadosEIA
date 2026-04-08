package com.juanpablo0612.carpool.domain.routes.model

sealed class RouteError {
    object EmptyOrigin : RouteError()
    object EmptyDestination : RouteError()
    object NoWaypoints : RouteError()
    object NoDaysSelected : RouteError()
    object InvalidTime : RouteError()
    object RouteNotFound : RouteError()
    object Unauthorized : RouteError()
    object UnknownError : RouteError()
}
