package com.juanpablo0612.carpool.data.booking.datasource

import com.juanpablo0612.carpool.data.booking.model.BookingDto
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseBookingRemoteDataSource(
    private val firestore: FirebaseFirestore
) : BookingRemoteDataSource {

    override suspend fun createBooking(booking: BookingDto): BookingDto {
        val docRef = firestore.collection(COLLECTION_NAME).document
        val dto = booking.copy(id = docRef.id)
        docRef.set(BookingDto.serializer(), dto)
        return dto
    }

    override fun getPassengerBookings(passengerId: String): Flow<List<BookingDto>> {
        return firestore.collection(COLLECTION_NAME)
            .where { "passengerId" equalTo passengerId }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(BookingDto.serializer()) }
            }
    }

    override fun getDriverBookingRequests(driverId: String): Flow<List<BookingDto>> {
        return firestore.collection(COLLECTION_NAME)
            .where {
                all(
                    "driverId" equalTo driverId,
                    "status" equalTo "PENDING",
                )
            }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(BookingDto.serializer()) }
            }
    }

    override fun getAllDriverBookings(driverId: String): Flow<List<BookingDto>> {
        return firestore.collection(COLLECTION_NAME)
            .where { "driverId" equalTo driverId }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(BookingDto.serializer()) }
            }
    }

    // Full CONFIRMED passenger list for the trip's own driver — used by TripTrackingViewModel to
    // render pickup status per passenger. Scoping driverId server-side (instead of client-side)
    // makes the query itself provably satisfy the `bookings` read rule.
    override fun getBookingsForTripAsDriver(tripId: String, driverId: String): Flow<List<BookingDto>> {
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
                snapshot.documents.map { it.data(BookingDto.serializer()) }
            }
    }

    // A single passenger's own CONFIRMED booking on the trip (0 or 1 item) — used by
    // TripTrackingViewModel on the passenger side to find their chat/booking id. Seat *counts*
    // never read bookings at all anymore; see GetTripAvailableSeatsUseCase.
    override fun getBookingsForTripAsPassenger(tripId: String, passengerId: String): Flow<List<BookingDto>> {
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
                snapshot.documents.map { it.data(BookingDto.serializer()) }
            }
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
    override suspend fun applyStatusTransition(
        bookingId: String,
        newStatus: String,
        extraFields: List<Pair<String, Any?>>,
    ) {
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
    }

    override suspend fun hasActiveBooking(passengerId: String, tripId: String): Boolean {
        val snapshot = firestore.collection(COLLECTION_NAME)
            .where {
                all(
                    "passengerId" equalTo passengerId,
                    "tripId" equalTo tripId,
                    "status" inArray listOf("PENDING", "CONFIRMED"),
                )
            }
            .get()
        return snapshot.documents.isNotEmpty()
    }

    companion object {
        private const val COLLECTION_NAME = "bookings"
        private const val TRIPS_COLLECTION_NAME = "trips"
    }
}
