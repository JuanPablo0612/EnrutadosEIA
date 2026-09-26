package com.juanpablo0612.carpool.data.route.datasource

import com.juanpablo0612.carpool.data.route.model.RouteDto
import kotlinx.coroutines.flow.Flow

interface RouteRemoteDataSource {
    suspend fun createRoute(route: RouteDto): RouteDto
    fun getUserRoutes(userId: String): Flow<List<RouteDto>>
    suspend fun getRouteById(id: String): RouteDto
    suspend fun updateRoute(route: RouteDto)
    suspend fun deleteRoute(id: String)
}
