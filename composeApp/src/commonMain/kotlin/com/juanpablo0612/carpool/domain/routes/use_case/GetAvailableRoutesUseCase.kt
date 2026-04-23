package com.juanpablo0612.carpool.domain.routes.use_case

import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.domain.routes.repository.RouteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAvailableRoutesUseCase(
    private val routeRepository: RouteRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<List<Route>> {
        val currentUserId = authRepository.getCurrentUserId()
        return routeRepository.getAvailableRoutes()
            .map { routes -> routes.filter { it.driverId != currentUserId } }
    }
}
