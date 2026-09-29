package com.juanpablo0612.carpool.domain.booking.usecase

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.model.RejectReason
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import kotlinx.coroutines.flow.Flow

private fun unused(): Nothing = error("not used by these tests")

/** Records every write; reads other than [hasActiveBooking] are not used by the tests. */
class FakeBookingRepository(
    private val hasActive: Boolean = false,
    private val failWrites: Boolean = false,
) : BookingRepository {
    val created = mutableListOf<Booking>()
    val statusChanges = mutableListOf<Pair<String, BookingStatus>>()
    val rejections = mutableListOf<Triple<String, RejectReason, String?>>()

    override suspend fun createBooking(booking: Booking): Result<Unit> {
        created += booking
        return Result.success(Unit)
    }

    override suspend fun hasActiveBooking(passengerId: String, tripId: String) = Result.success(hasActive)

    override suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit> {
        if (failWrites) return Result.failure(AppException.BookingException.Unknown)
        statusChanges += bookingId to status
        return Result.success(Unit)
    }

    override suspend fun rejectBookingWithReason(bookingId: String, reason: RejectReason, comment: String?): Result<Unit> {
        if (failWrites) return Result.failure(AppException.BookingException.Unknown)
        rejections += Triple(bookingId, reason, comment)
        return Result.success(Unit)
    }

    override fun getPassengerBookings(passengerId: String): Flow<List<Booking>> = unused()
    override fun getDriverBookingRequests(driverId: String): Flow<List<Booking>> = unused()
    override fun getOpenDriverBookings(driverId: String, departingAfter: Long): Flow<List<Booking>> = unused()
    override fun getOpenBookingsForTrip(tripId: String, driverId: String): Flow<List<Booking>> = unused()
    override fun getBookingsForTripAsDriver(tripId: String, driverId: String): Flow<List<Booking>> = unused()
    override fun getBookingsForTripAsPassenger(tripId: String, passengerId: String): Flow<List<Booking>> = unused()
}
