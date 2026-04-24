package com.juanpablo0612.carpool.domain.routes.use_case

import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.domain.routes.repository.RouteRepository

class CreateRouteUseCase(private val repository: RouteRepository) {
    suspend operator fun invoke(route: Route): Result<Unit> = repository.createRoute(route)
}
