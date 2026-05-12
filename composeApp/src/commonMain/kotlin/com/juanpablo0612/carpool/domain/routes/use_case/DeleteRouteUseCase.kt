package com.juanpablo0612.carpool.domain.routes.use_case

import com.juanpablo0612.carpool.domain.routes.repository.RouteRepository

class DeleteRouteUseCase(private val repository: RouteRepository) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deleteRoute(id)
}
