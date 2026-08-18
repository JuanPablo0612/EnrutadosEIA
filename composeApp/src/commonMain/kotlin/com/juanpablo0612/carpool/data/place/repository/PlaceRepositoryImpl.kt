package com.juanpablo0612.carpool.data.place.repository

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.data.place.model.PlaceDto
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.place.repository.PlaceRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaceRepositoryImpl(
    private val firestore: FirebaseFirestore
) : PlaceRepository {

    override suspend fun deletePlace(placeId: String): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_NAME).document(placeId).delete()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppException.PlaceException.Unknown)
        }
    }

    @OptIn(kotlin.uuid.ExperimentalUuidApi::class)
    override suspend fun createPlace(place: Place): Result<Unit> {
        return try {
            val collectionRef = firestore.collection(COLLECTION_NAME)
            val docRef = collectionRef.document(kotlin.uuid.Uuid.random().toString())
            val dto = PlaceDto.fromDomain(place).copy(id = docRef.id)
            docRef.set(PlaceDto.serializer(), dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppException.PlaceException.Unknown)
        }
    }

    override fun getSavedPlaces(ownerId: String): Flow<List<Place>> {
        return firestore.collection(COLLECTION_NAME)
            .where { "ownerId" equalTo ownerId }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    doc.data(PlaceDto.serializer()).toDomain()
                }
            }
    }

    companion object {
        private const val COLLECTION_NAME = "places"
    }
}
