package com.juanpablo0612.carpool.domain.route.usecase

import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.domain.route.repository.RouteRepository

class UpdateRouteUseCase(private val repository: RouteRepository) {
    suspend operator fun invoke(route: Route): Result<Unit> = repository.updateRoute(route)
}
