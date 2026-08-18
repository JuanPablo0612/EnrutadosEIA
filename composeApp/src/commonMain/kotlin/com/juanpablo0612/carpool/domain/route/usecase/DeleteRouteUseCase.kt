package com.juanpablo0612.carpool.domain.route.usecase

import com.juanpablo0612.carpool.domain.route.repository.RouteRepository

class DeleteRouteUseCase(private val repository: RouteRepository) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deleteRoute(id)
}
