package com.juanpablo0612.carpool.domain.routes.use_case

import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.domain.routes.repository.RouteRepository

class GetRouteByIdUseCase(private val repository: RouteRepository) {
    suspend operator fun invoke(id: String): Result<Route> = repository.getRouteById(id)
}
