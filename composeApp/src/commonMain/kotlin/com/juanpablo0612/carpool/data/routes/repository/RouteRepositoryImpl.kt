package com.juanpablo0612.carpool.data.routes.repository

import com.juanpablo0612.carpool.data.routes.model.RouteDto
import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.domain.routes.repository.RouteRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore

class RouteRepositoryImpl(
    private val firestore: FirebaseFirestore
) : RouteRepository {

    override suspend fun createRoute(route: Route): Result<Unit> {
        return try {
            val routeDto = RouteDto.fromDomain(route)
            val documentReference = firestore.collection("routes").document
            val finalDto = routeDto.copy(id = documentReference.id)
            
            documentReference.set(RouteDto.serializer(), finalDto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
