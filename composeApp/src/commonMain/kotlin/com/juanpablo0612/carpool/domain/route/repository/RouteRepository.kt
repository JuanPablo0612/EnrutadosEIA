package com.juanpablo0612.carpool.domain.route.repository

import com.juanpablo0612.carpool.domain.route.model.Route
import kotlinx.coroutines.flow.Flow

interface RouteRepository {
    /** Creates the route and returns its id. */
    suspend fun createRoute(route: Route): Result<String>
    fun getUserRoutes(userId: String): Flow<List<Route>>
    suspend fun getRouteById(id: String): Result<Route>
    suspend fun updateRoute(route: Route): Result<Unit>
    suspend fun deleteRoute(id: String): Result<Unit>
}
