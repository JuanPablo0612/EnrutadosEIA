package com.juanpablo0612.carpool.domain.route.usecase

import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.domain.route.repository.RouteRepository

class GetRouteByIdUseCase(private val repository: RouteRepository) {
    suspend operator fun invoke(id: String): Result<Route> = repository.getRouteById(id)
}
