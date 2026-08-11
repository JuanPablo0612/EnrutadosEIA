package com.juanpablo0612.carpool.data.places.repository

import com.juanpablo0612.carpool.data.places.model.PlaceDto
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.domain.places.repository.PlacesRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlacesRepositoryImpl(
    private val firestore: FirebaseFirestore
) : PlacesRepository {

    override suspend fun deletePlace(placeId: String): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_NAME).document(placeId).delete()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
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
            Result.failure(e)
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

    // Firestore cannot do substring matching, so the name/address text match still happens
    // client-side; only the ownerId scoping (required by firestore.rules) runs server-side.
    override suspend fun searchPlaces(ownerId: String, query: String): Result<List<Place>> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME)
                .where { "ownerId" equalTo ownerId }
                .get()
            val filtered = snapshot.documents
                .map { it.data(PlaceDto.serializer()).toDomain() }
                .filter {
                    it.name.contains(query, ignoreCase = true) ||
                    it.address.contains(query, ignoreCase = true)
                }
            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val COLLECTION_NAME = "places"
    }
}
