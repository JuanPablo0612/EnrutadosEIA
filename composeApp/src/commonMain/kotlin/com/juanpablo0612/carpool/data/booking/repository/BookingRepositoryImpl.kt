package com.juanpablo0612.carpool.data.booking.repository

import com.juanpablo0612.carpool.data.booking.model.BookingDto
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.model.RejectReason
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookingRepositoryImpl(
    private val firestore: FirebaseFirestore
) : BookingRepository {

    override suspend fun createBooking(booking: Booking): Result<Unit> {
        return try {
            val docRef = firestore.collection(COLLECTION_NAME).document
            val dto = BookingDto.fromDomain(booking).copy(id = docRef.id)
            docRef.set(BookingDto.serializer(), dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPassengerBookings(passengerId: String): Flow<List<Booking>> {
        return firestore.collection(COLLECTION_NAME)
            .where { "passengerId" equalTo passengerId }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(BookingDto.serializer()).toDomain() }
            }
    }

    override fun getDriverBookingRequests(driverId: String): Flow<List<Booking>> {
        return firestore.collection(COLLECTION_NAME)
            .where {
                all(
                    "driverId" equalTo driverId,
                    "status" equalTo "PENDING",
                )
            }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(BookingDto.serializer()).toDomain() }
            }
    }

    override fun getAllDriverBookings(driverId: String): Flow<List<Booking>> {
        return firestore.collection(COLLECTION_NAME)
            .where { "driverId" equalTo driverId }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(BookingDto.serializer()).toDomain() }
            }
    }

    // Only CONFIRMED bookings occupy a seat or ride along on an active trip, which is all that
    // GetTripAvailableSeatsUseCase / TripTrackingViewModel ever read from this flow. Scoping the
    // status here (instead of client-side) keeps the query self-contained for callers who are not
    // yet a party to any booking on this trip (e.g. a passenger still browsing).
    override fun getBookingsForTrip(tripId: String): Flow<List<Booking>> {
        return firestore.collection(COLLECTION_NAME)
            .where {
                all(
                    "tripId" equalTo tripId,
                    "status" equalTo "CONFIRMED",
                )
            }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(BookingDto.serializer()).toDomain() }
            }
    }

    override suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit> {
        return try {
            val statusString = when (status) {
                is BookingStatus.Pending -> "PENDING"
                is BookingStatus.Confirmed -> "CONFIRMED"
                is BookingStatus.Rejected -> "REJECTED"
                is BookingStatus.Cancelled -> "CANCELLED"
            }
            firestore.collection(COLLECTION_NAME).document(bookingId)
                .update("status" to statusString)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rejectBookingWithReason(
        bookingId: String,
        reason: RejectReason,
        comment: String?,
    ): Result<Unit> {
        return try {
            val reasonString = when (reason) {
                RejectReason.TripFull -> "TRIP_FULL"
                RejectReason.TripCancelled -> "TRIP_CANCELLED"
                RejectReason.PickupNotPossible -> "PICKUP_NOT_POSSIBLE"
                RejectReason.Other -> "OTHER"
            }
            val updates = buildMap {
                put("status", "REJECTED")
                put("rejectReason", reasonString)
                if (comment != null) put("rejectComment", comment)
            }
            firestore.collection(COLLECTION_NAME).document(bookingId).update(updates)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun hasActiveBooking(passengerId: String, tripId: String): Result<Boolean> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME)
                .where {
                    all(
                        "passengerId" equalTo passengerId,
                        "tripId" equalTo tripId,
                        "status" inArray listOf("PENDING", "CONFIRMED"),
                    )
                }
                .get()
            Result.success(snapshot.documents.isNotEmpty())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val COLLECTION_NAME = "bookings"
    }
}
