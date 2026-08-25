package com.juanpablo0612.carpool.data.route.repository

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.data.route.datasource.RouteRemoteDataSource
import com.juanpablo0612.carpool.data.route.model.RouteDto
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.domain.route.repository.RouteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RouteRepositoryImpl(
    private val remoteDataSource: RouteRemoteDataSource
) : RouteRepository {

    override suspend fun createRoute(route: Route): Result<Unit> {
        return try {
            val dto = RouteDto.fromDomain(route)
            remoteDataSource.createRoute(dto)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.RouteException.Unknown)
        }
    }

    override fun getUserRoutes(userId: String): Flow<List<Route>> {
        return remoteDataSource.getUserRoutes(userId)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getRouteById(id: String): Result<Route> {
        return try {
            val route = remoteDataSource.getRouteById(id).toDomain()
            Result.success(route)
        } catch (_: Exception) {
            Result.failure(AppException.RouteException.Unknown)
        }
    }

    override suspend fun updateRoute(route: Route): Result<Unit> {
        return try {
            val dto = RouteDto.fromDomain(route)
            remoteDataSource.updateRoute(dto)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.RouteException.Unknown)
        }
    }

    override suspend fun deleteRoute(id: String): Result<Unit> {
        return try {
            remoteDataSource.deleteRoute(id)
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppException.RouteException.Unknown)
        }
    }
}
