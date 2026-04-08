package com.juanpablo0612.carpool.domain.routes.repository

import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.domain.routes.model.RouteError

interface RouteRepository {
    suspend fun createRoute(route: Route): Result<Unit>
}
