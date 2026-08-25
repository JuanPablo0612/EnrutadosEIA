package com.juanpablo0612.carpool.data.route.datasource

import com.juanpablo0612.carpool.data.route.model.RouteDto
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseRouteRemoteDataSource(
    private val firestore: FirebaseFirestore
) : RouteRemoteDataSource {

    override suspend fun createRoute(route: RouteDto): RouteDto {
        val docRef = firestore.collection(COLLECTION_NAME).document
        val dto = route.copy(id = docRef.id)
        docRef.set(RouteDto.serializer(), dto)
        return dto
    }

    override fun getUserRoutes(userId: String): Flow<List<RouteDto>> {
        return firestore.collection(COLLECTION_NAME)
            .where { "driverId" equalTo userId }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(RouteDto.serializer()) }
            }
    }

    override suspend fun getRouteById(id: String): RouteDto {
        val snapshot = firestore.collection(COLLECTION_NAME).document(id).get()
        return snapshot.data(RouteDto.serializer())
    }

    override suspend fun updateRoute(route: RouteDto) {
        firestore.collection(COLLECTION_NAME).document(route.id).set(RouteDto.serializer(), route)
    }

    override suspend fun deleteRoute(id: String) {
        firestore.collection(COLLECTION_NAME).document(id).delete()
    }

    companion object {
        private const val COLLECTION_NAME = "routes"
    }
}
