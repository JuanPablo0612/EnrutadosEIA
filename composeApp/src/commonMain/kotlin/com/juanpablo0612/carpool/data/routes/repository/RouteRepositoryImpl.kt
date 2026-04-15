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
            val docRef = firestore.collection(COLLECTION_NAME).document
            val dto = RouteDto.fromDomain(route).copy(id = docRef.id)
            docRef.set(RouteDto.serializer(), dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val COLLECTION_NAME = "routes"
    }
}
