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
            val collectionRef = firestore.collection("places")
            val docRef = collectionRef.document(kotlin.uuid.Uuid.random().toString())
            val dto = PlaceDto.fromDomain(place).copy(id = docRef.id)
            docRef.set(PlaceDto.serializer(), dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getSavedPlaces(): Flow<List<Place>> {
        return firestore.collection("places")
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    doc.data(PlaceDto.serializer()).toDomain()
                }
            }
    }

    override suspend fun searchPlaces(query: String): Result<List<Place>> {
        return try {
            // Fetching all places and filtering locally for simple prefix/contains search
            val snapshot = firestore.collection("places").get()
            val allPlaces = snapshot.documents.map { it.data(PlaceDto.serializer()).toDomain() }
            val filtered = allPlaces.filter { 
                it.name.contains(query, ignoreCase = true) || 
                it.address.contains(query, ignoreCase = true) 
            }
            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
