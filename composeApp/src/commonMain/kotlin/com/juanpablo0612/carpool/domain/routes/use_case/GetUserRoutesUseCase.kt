package com.juanpablo0612.carpool.domain.routes.use_case

import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.domain.routes.repository.RouteRepository
import kotlinx.coroutines.flow.Flow

class GetUserRoutesUseCase(private val repository: RouteRepository) {
    operator fun invoke(userId: String): Flow<List<Route>> = repository.getUserRoutes(userId)
}
