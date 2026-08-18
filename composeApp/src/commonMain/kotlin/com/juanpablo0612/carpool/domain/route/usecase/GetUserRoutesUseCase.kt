package com.juanpablo0612.carpool.domain.route.usecase

import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.domain.route.repository.RouteRepository
import kotlinx.coroutines.flow.Flow

class GetUserRoutesUseCase(private val repository: RouteRepository) {
    operator fun invoke(userId: String): Flow<List<Route>> = repository.getUserRoutes(userId)
}
