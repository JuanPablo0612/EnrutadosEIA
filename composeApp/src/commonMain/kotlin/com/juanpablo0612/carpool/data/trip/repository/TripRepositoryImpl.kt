package com.juanpablo0612.carpool.data.trip.repository

import com.juanpablo0612.carpool.data.trip.model.TripDto
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

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
            .where { "driverId" equalTo driverId }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(TripDto.serializer()).toDomain() }
            }
    }

    override fun getAvailableTrips(): Flow<List<Trip>> {
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

    override suspend fun updateTripStatus(tripId: String, status: TripStatus): Result<Unit> {
        return try {
            val statusString = when (status) {
                is TripStatus.Active -> "ACTIVE"
                is TripStatus.InProgress -> "IN_PROGRESS"
                is TripStatus.Completed -> "COMPLETED"
                is TripStatus.Cancelled -> "CANCELLED"
            }
            firestore.collection(COLLECTION_NAME).document(tripId)
                .update("status" to statusString)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getTripByIdFlow(id: String): Flow<Trip?> {
        return firestore.collection(COLLECTION_NAME).document(id)
            .snapshots
            .map { snapshot ->
                runCatching { snapshot.data(TripDto.serializer()).toDomain() }.getOrNull()
            }
    }

    override suspend fun updateDriverLocation(tripId: String, latitude: Double, longitude: Double): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_NAME).document(tripId)
                .update("driverLatitude" to latitude, "driverLongitude" to longitude)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePassengerStatus(tripId: String, passengerId: String, status: String): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_NAME).document(tripId)
                .update("passengerStatuses.$passengerId" to status)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val COLLECTION_NAME = "trips"
    }
}
