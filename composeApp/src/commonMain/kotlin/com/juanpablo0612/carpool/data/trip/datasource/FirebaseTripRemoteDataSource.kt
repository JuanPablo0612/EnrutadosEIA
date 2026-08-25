package com.juanpablo0612.carpool.data.trip.datasource

import com.juanpablo0612.carpool.data.trip.model.TripDto
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class FirebaseTripRemoteDataSource(
    private val firestore: FirebaseFirestore
) : TripRemoteDataSource {

    override suspend fun createTrip(trip: TripDto): TripDto {
        val docRef = firestore.collection(COLLECTION_NAME).document
        val dto = trip.copy(id = docRef.id)
        docRef.set(TripDto.serializer(), dto)
        return dto
    }

    override fun getDriverTrips(driverId: String): Flow<List<TripDto>> {
        return firestore.collection(COLLECTION_NAME)
            .where { "driverId" equalTo driverId }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(TripDto.serializer()) }
            }
    }

    override fun getAvailableTrips(): Flow<List<TripDto>> {
        val now = Clock.System.now().toEpochMilliseconds()
        return firestore.collection(COLLECTION_NAME)
            .where {
                all(
                    "status" equalTo "ACTIVE",
                    "departureTime" greaterThanOrEqualTo now,
                )
            }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(TripDto.serializer()) }
            }
    }

    override suspend fun getTripById(id: String): TripDto {
        val snapshot = firestore.collection(COLLECTION_NAME).document(id).get()
        return snapshot.data(TripDto.serializer())
    }

    override fun getTripByIdFlow(id: String): Flow<TripDto?> {
        return firestore.collection(COLLECTION_NAME).document(id)
            .snapshots
            .map { snapshot ->
                runCatching { snapshot.data(TripDto.serializer()) }.getOrNull()
            }
    }

    override suspend fun updateTripStatus(tripId: String, status: String) {
        firestore.collection(COLLECTION_NAME).document(tripId)
            .update("status" to status)
    }

    override suspend fun updateDriverLocation(tripId: String, latitude: Double, longitude: Double) {
        firestore.collection(COLLECTION_NAME).document(tripId)
            .update("driverLatitude" to latitude, "driverLongitude" to longitude)
    }

    override suspend fun updatePassengerStatus(tripId: String, passengerId: String, status: String) {
        firestore.collection(COLLECTION_NAME).document(tripId)
            .update("passengerStatuses.$passengerId" to status)
    }

    companion object {
        private const val COLLECTION_NAME = "trips"
    }
}
