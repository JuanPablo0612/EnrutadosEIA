package com.juanpablo0612.carpool.domain.routes.model

import kotlinx.serialization.Serializable

@Serializable
sealed class RouteType {
    @Serializable
    data object ToUniversity : RouteType()

    @Serializable
    data object FromUniversity : RouteType()
}
