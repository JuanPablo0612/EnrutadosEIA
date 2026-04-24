package com.juanpablo0612.carpool.data.trip.repository

import com.juanpablo0612.carpool.data.trip.model.TripDto
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TripRepositoryImpl(
    private val firestore: FirebaseFirestore
) : TripRepository {

    override suspend fun createTrip(trip: Trip): Result<Unit> {
        return try {
            val docRef = firestore.collection(COLLECTION_NAME).document
            val dto = TripDto.fromDomain(trip).copy(id = docRef.id)
            docRef.set(TripDto.serializer(), dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getDriverTrips(driverId: String): Flow<List<Trip>> {
        return firestore.collection(COLLECTION_NAME)
            .snapshots
            .map { snapshot ->
                snapshot.documents
                    .map { it.data(TripDto.serializer()).toDomain() }
                    .filter { it.driverId == driverId }
            }
    }

    override fun getAvailableTrips(): Flow<List<Trip>> {
        return firestore.collection(COLLECTION_NAME)
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(TripDto.serializer()).toDomain() }
            }
    }

    override suspend fun getTripById(id: String): Result<Trip> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME).document(id).get()
            val trip = snapshot.data(TripDto.serializer()).toDomain()
            Result.success(trip)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val COLLECTION_NAME = "trips"
    }
}
