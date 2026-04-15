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

    override fun getSavedPlaces(): Flow<List<Place>> {
        return firestore.collection(COLLECTION_NAME)
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    doc.data(PlaceDto.serializer()).toDomain()
                }
            }
    }

    // Fetches all places and filters locally; suitable for small datasets
    override suspend fun searchPlaces(query: String): Result<List<Place>> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME).get()
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
