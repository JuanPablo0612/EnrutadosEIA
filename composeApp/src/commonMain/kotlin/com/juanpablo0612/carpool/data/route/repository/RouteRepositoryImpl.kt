package com.juanpablo0612.carpool.data.route.repository

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.data.route.model.RouteDto
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.domain.route.repository.RouteRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RouteRepositoryImpl(
    private val firestore: FirebaseFirestore
) : RouteRepository {

    override suspend fun createRoute(route: Route): Result<Unit> {
        return try {
            val docRef = firestore.collection(COLLECTION_NAME).document
            val dto = RouteDto.fromDomain(route).copy(id = docRef.id)
            docRef.set(RouteDto.serializer(), dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppException.RouteException.Unknown)
        }
    }

    override fun getUserRoutes(userId: String): Flow<List<Route>> {
        return firestore.collection(COLLECTION_NAME)
            .where { "driverId" equalTo userId }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(RouteDto.serializer()).toDomain() }
            }
    }

    override suspend fun getRouteById(id: String): Result<Route> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME).document(id).get()
            val route = snapshot.data(RouteDto.serializer()).toDomain()
            Result.success(route)
        } catch (e: Exception) {
            Result.failure(AppException.RouteException.Unknown)
        }
    }

    override suspend fun updateRoute(route: Route): Result<Unit> {
        return try {
            val dto = RouteDto.fromDomain(route)
            firestore.collection(COLLECTION_NAME).document(route.id).set(RouteDto.serializer(), dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppException.RouteException.Unknown)
        }
    }

    override suspend fun deleteRoute(id: String): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_NAME).document(id).delete()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppException.RouteException.Unknown)
        }
    }

    companion object {
        private const val COLLECTION_NAME = "routes"
    }
}
