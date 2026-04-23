package com.juanpablo0612.carpool.domain.routes.use_case

import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.domain.routes.repository.RouteRepository

class CreateRouteUseCase(private val repository: RouteRepository) {
    suspend operator fun invoke(route: Route): Result<Unit> {
        if (route.waypoints.isEmpty()) {
            return Result.failure(Exception("At least one intermediate place is required"))
        }
        
        if (route.daysOfWeek.isEmpty()) {
            return Result.failure(Exception("At least one day must be selected"))
        }

        return repository.createRoute(route)
    }
}
