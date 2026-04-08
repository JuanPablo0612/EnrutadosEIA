package com.juanpablo0612.carpool.domain.routes.model

import kotlinx.serialization.Serializable

@Serializable
sealed class RouteType {
    @Serializable
    data object ToUniversity : RouteType()
    
    @Serializable
    data object FromUniversity : RouteType()

    fun isToUniversity() = this is ToUniversity
    fun isFromUniversity() = this is FromUniversity
}
