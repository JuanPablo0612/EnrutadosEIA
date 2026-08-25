package com.juanpablo0612.carpool.data.place.datasource

import com.juanpablo0612.carpool.data.place.model.PlaceDto
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebasePlaceRemoteDataSource(
    private val firestore: FirebaseFirestore
) : PlaceRemoteDataSource {

    override suspend fun deletePlace(placeId: String) {
        firestore.collection(COLLECTION_NAME).document(placeId).delete()
    }

    @OptIn(kotlin.uuid.ExperimentalUuidApi::class)
    override suspend fun createPlace(place: PlaceDto): PlaceDto {
        val collectionRef = firestore.collection(COLLECTION_NAME)
        val docRef = collectionRef.document(kotlin.uuid.Uuid.random().toString())
        val dto = place.copy(id = docRef.id)
        docRef.set(PlaceDto.serializer(), dto)
        return dto
    }

    override fun getSavedPlaces(ownerId: String): Flow<List<PlaceDto>> {
        return firestore.collection(COLLECTION_NAME)
            .where { "ownerId" equalTo ownerId }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    doc.data(PlaceDto.serializer())
                }
            }
    }

    companion object {
        private const val COLLECTION_NAME = "places"
    }
}
