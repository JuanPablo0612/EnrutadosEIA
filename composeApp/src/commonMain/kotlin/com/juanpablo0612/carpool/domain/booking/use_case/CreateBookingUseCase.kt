package com.juanpablo0612.carpool.domain.booking.use_case

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import kotlinx.coroutines.flow.first
import kotlin.time.Clock

class CreateBookingUseCase(
    private val bookingRepository: BookingRepository,
    private val authRepository: AuthRepository,
    private val getVehicleAvailableSeatsUseCase: GetVehicleAvailableSeatsUseCase
) {
    suspend operator fun invoke(
        routeId: String,
        vehicleId: String,
        driverId: String,
        totalSeats: Int
    ): Result<Unit> {
        val user = authRepository.getCurrentUser().getOrElse {
            return Result.failure(AppException.BookingException.NotAuthenticated)
        }

        val alreadyBooked = bookingRepository.hasActiveBooking(user.id, routeId, vehicleId)
            .getOrElse { return Result.failure(AppException.BookingException.Unknown) }
        if (alreadyBooked) {
            return Result.failure(AppException.BookingException.AlreadyBooked)
        }

        val availableSeats = getVehicleAvailableSeatsUseCase(routeId, vehicleId, totalSeats).first()
        if (availableSeats <= 0) {
            return Result.failure(AppException.BookingException.NoSeatsAvailable)
        }

        val booking = Booking(
            routeId = routeId,
            vehicleId = vehicleId,
            passengerId = user.id,
            driverId = driverId,
            passengerName = user.name.orEmpty(),
            passengerEmail = user.email,
            status = BookingStatus.Pending,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
        return bookingRepository.createBooking(booking)
    }
}
