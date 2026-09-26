package com.juanpablo0612.carpool.domain.route.usecase

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.domain.route.repository.RouteRepository

class DuplicateRouteUseCase(
    private val repository: RouteRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(route: Route, nameOverride: String? = null): Result<Unit> {
        val currentUserId = authRepository.getCurrentUserId()
            ?: return Result.failure(AppException.RouteException.NotAuthenticated)

        val duplicate = route.copy(
            id = "",
            driverId = currentUserId,
            name = nameOverride ?: route.name,
        )
        return repository.createRoute(duplicate).map { }
    }
}
