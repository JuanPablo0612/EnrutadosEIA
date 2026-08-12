package com.juanpablo0612.carpool.data.booking.repository

import com.juanpablo0612.carpool.data.booking.model.BookingDto
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.model.RejectReason
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import dev.gitlive.firebase.firestore.FieldValue
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

    // Full CONFIRMED passenger list for the trip's own driver — used by TripTrackingViewModel to
    // render pickup status per passenger. Scoping driverId server-side (instead of client-side)
    // makes the query itself provably satisfy the `bookings` read rule.
    override fun getBookingsForTripAsDriver(tripId: String, driverId: String): Flow<List<Booking>> {
        return firestore.collection(COLLECTION_NAME)
            .where {
                all(
                    "tripId" equalTo tripId,
                    "driverId" equalTo driverId,
                    "status" equalTo "CONFIRMED",
                )
            }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(BookingDto.serializer()).toDomain() }
            }
    }

    // A single passenger's own CONFIRMED booking on the trip (0 or 1 item) — used by
    // TripTrackingViewModel on the passenger side to find their chat/booking id. Seat *counts*
    // never read bookings at all anymore; see GetTripAvailableSeatsUseCase.
    override fun getBookingsForTripAsPassenger(tripId: String, passengerId: String): Flow<List<Booking>> {
        return firestore.collection(COLLECTION_NAME)
            .where {
                all(
                    "tripId" equalTo tripId,
                    "passengerId" equalTo passengerId,
                    "status" equalTo "CONFIRMED",
                )
            }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(BookingDto.serializer()).toDomain() }
            }
    }

    override suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit> {
        val statusString = when (status) {
            is BookingStatus.Pending -> "PENDING"
            is BookingStatus.Confirmed -> "CONFIRMED"
            is BookingStatus.Rejected -> "REJECTED"
            is BookingStatus.Cancelled -> "CANCELLED"
        }
        return applyStatusTransition(bookingId, statusString)
    }

    override suspend fun rejectBookingWithReason(
        bookingId: String,
        reason: RejectReason,
        comment: String?,
    ): Result<Unit> {
        val reasonString = when (reason) {
            RejectReason.TripFull -> "TRIP_FULL"
            RejectReason.TripCancelled -> "TRIP_CANCELLED"
            RejectReason.PickupNotPossible -> "PICKUP_NOT_POSSIBLE"
            RejectReason.Other -> "OTHER"
        }
        val extraFields = buildList<Pair<String, Any?>> {
            add("rejectReason" to reasonString)
            if (comment != null) add("rejectComment" to comment)
        }
        return applyStatusTransition(bookingId, "REJECTED", extraFields)
    }

    // Applies a booking status change and keeps trips/{tripId}.confirmedSeats in sync with it.
    //
    // Concurrency safety has two parts:
    // - The booking is read *inside* the transaction, so Firestore's optimistic-concurrency retry
    //   guards the transition itself: if two calls race on the same booking (e.g. a double tap),
    //   only the first commits from PENDING; the retried transaction re-reads the now-CONFIRMED
    //   booking and sees `entersConfirmed == false`, so it never increments a second time.
    // - The counter write itself uses FieldValue.increment rather than read-modify-write, so two
    //   *different* bookings on the same trip transitioning concurrently (e.g. two drivers on a
    //   shared account accepting two requests at once) each apply their own atomic +1/-1 without
    //   forcing each other to retry or clobbering one another's write.
    //
    // Only transitions into or out of CONFIRMED touch the counter, so a booking that is REJECTED
    // and later CANCELLED (never having been CONFIRMED) is never decremented twice.
    private suspend fun applyStatusTransition(
        bookingId: String,
        newStatus: String,
        extraFields: List<Pair<String, Any?>> = emptyList(),
    ): Result<Unit> {
        return try {
            firestore.runTransaction {
                val bookingRef = firestore.collection(COLLECTION_NAME).document(bookingId)
                val booking = get(bookingRef).data(BookingDto.serializer())

                val entersConfirmed = newStatus == "CONFIRMED" && booking.status != "CONFIRMED"
                val exitsConfirmed = booking.status == "CONFIRMED" && newStatus != "CONFIRMED"
                if (entersConfirmed || exitsConfirmed) {
                    val tripRef = firestore.collection(TRIPS_COLLECTION_NAME).document(booking.tripId)
                    val delta = if (entersConfirmed) 1 else -1
                    update(tripRef, "confirmedSeats" to FieldValue.increment(delta))
                }

                val bookingFields = (listOf<Pair<String, Any?>>("status" to newStatus) + extraFields)
                update(bookingRef, *bookingFields.toTypedArray())
            }
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
        private const val TRIPS_COLLECTION_NAME = "trips"
    }
}
