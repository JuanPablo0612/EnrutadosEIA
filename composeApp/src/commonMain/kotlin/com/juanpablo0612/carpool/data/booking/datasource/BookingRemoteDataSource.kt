package com.juanpablo0612.carpool.data.booking.datasource

import com.juanpablo0612.carpool.data.booking.model.BookingDto
import kotlinx.coroutines.flow.Flow

interface BookingRemoteDataSource {
    suspend fun createBooking(booking: BookingDto): BookingDto
    fun getPassengerBookings(passengerId: String): Flow<List<BookingDto>>
    fun getDriverBookingRequests(driverId: String): Flow<List<BookingDto>>
    fun getAllDriverBookings(driverId: String): Flow<List<BookingDto>>

    // Both party-scoped so the `bookings` read rule (passengerId == uid || driverId == uid) is
    // statically provable from the query itself: the driver stream returns every CONFIRMED
    // passenger on the trip, the passenger stream returns only their own (0 or 1 item).
    fun getBookingsForTripAsDriver(tripId: String, driverId: String): Flow<List<BookingDto>>
    fun getBookingsForTripAsPassenger(tripId: String, passengerId: String): Flow<List<BookingDto>>

    suspend fun applyStatusTransition(
        bookingId: String,
        newStatus: String,
        extraFields: List<Pair<String, Any?>> = emptyList(),
    )

    suspend fun hasActiveBooking(passengerId: String, tripId: String): Boolean
}
