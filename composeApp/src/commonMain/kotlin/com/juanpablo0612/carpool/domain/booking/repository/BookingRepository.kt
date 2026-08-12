package com.juanpablo0612.carpool.domain.booking.repository

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.model.RejectReason
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    suspend fun createBooking(booking: Booking): Result<Unit>
    fun getPassengerBookings(passengerId: String): Flow<List<Booking>>
    fun getDriverBookingRequests(driverId: String): Flow<List<Booking>>
    fun getAllDriverBookings(driverId: String): Flow<List<Booking>>

    // Both party-scoped so the `bookings` read rule (passengerId == uid || driverId == uid) is
    // statically provable from the query itself: the driver stream returns every CONFIRMED
    // passenger on the trip, the passenger stream returns only their own (0 or 1 item).
    fun getBookingsForTripAsDriver(tripId: String, driverId: String): Flow<List<Booking>>
    fun getBookingsForTripAsPassenger(tripId: String, passengerId: String): Flow<List<Booking>>

    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit>
    suspend fun rejectBookingWithReason(
        bookingId: String,
        reason: RejectReason,
        comment: String?,
    ): Result<Unit>
    suspend fun hasActiveBooking(passengerId: String, tripId: String): Result<Boolean>
}
