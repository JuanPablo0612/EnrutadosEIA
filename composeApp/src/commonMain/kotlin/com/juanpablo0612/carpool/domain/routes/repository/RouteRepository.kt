package com.juanpablo0612.carpool.domain.routes.repository

import com.juanpablo0612.carpool.domain.routes.model.Route
import kotlinx.coroutines.flow.Flow

interface RouteRepository {
    suspend fun createRoute(route: Route): Result<Unit>
    fun getUserRoutes(userId: String): Flow<List<Route>>
    suspend fun getRouteById(id: String): Result<Route>
    suspend fun updateRoute(route: Route): Result<Unit>
    fun getAvailableRoutes(): Flow<List<Route>>
}
