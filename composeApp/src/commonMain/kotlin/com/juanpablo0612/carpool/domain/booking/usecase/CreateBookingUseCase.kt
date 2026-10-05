package com.juanpablo0612.carpool.domain.booking.usecase

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingMeetingStop
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripMeetingStop
import com.juanpablo0612.carpool.domain.trip.model.acceptsBookings
import kotlinx.coroutines.flow.first
import kotlin.time.Clock

/**
 * Asks for a seat on [trip]. Checks the trip still takes bookings at [now], that the passenger has
 * no open request for it and that a seat is still free, then records the request with everything both parties' lists show: the driver as
 * the trip shows them, the passenger's name and photo, and the stop where they meet.
 */
class CreateBookingUseCase(
    private val bookingRepository: BookingRepository,
    private val authRepository: AuthRepository,
    private val getTripAvailableSeatsUseCase: GetTripAvailableSeatsUseCase
) {
    suspend operator fun invoke(
        trip: Trip,
        meetingStop: TripMeetingStop? = null,
        passengerMessage: String? = null,
        now: Long = Clock.System.now().toEpochMilliseconds(),
    ): Result<Unit> {
        if (!trip.acceptsBookings(now)) {
            return Result.failure(AppException.BookingException.TripClosed)
        }

        val user = authRepository.getCurrentUser().getOrElse {
            return Result.failure(AppException.BookingException.NotAuthenticated)
        }

        val alreadyBooked = bookingRepository.hasActiveBooking(user.id, trip.id)
            .getOrElse { return Result.failure(AppException.BookingException.Unknown) }
        if (alreadyBooked) {
            return Result.failure(AppException.BookingException.AlreadyBooked)
        }

        // Read fresh rather than trusting the trip passed in, which may be minutes old.
        val availableSeats = getTripAvailableSeatsUseCase(trip.id).first()
        if (availableSeats <= 0) {
            return Result.failure(AppException.BookingException.NoSeatsAvailable)
        }

        val path = listOf(trip.origin) + trip.waypoints + trip.destination
        val booking = Booking(
            tripId = trip.id,
            passengerId = user.id,
            driverId = trip.driverId,
            passengerName = user.name.orEmpty(),
            passengerEmail = user.email,
            passengerPhotoUrl = user.photoUrl,
            driver = trip.driver,
            meetingStop = meetingStop?.let { stop ->
                path.getOrNull(stop.pathIndex)?.let { BookingMeetingStop(name = it.name, isDropoff = stop.isDropoff) }
            },
            originName = trip.origin.name,
            destinationName = trip.destination.name,
            departureTime = trip.departureTime,
            status = BookingStatus.Pending,
            createdAt = now,
            passengerMessage = passengerMessage?.trim()?.ifEmpty { null },
        )
        return bookingRepository.createBooking(booking)
    }
}
