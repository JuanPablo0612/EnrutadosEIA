package com.juanpablo0612.carpool.domain.trip.usecase

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import com.juanpablo0612.carpool.domain.trip.validation.TripValidationError
import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository
import kotlinx.coroutines.flow.first
import kotlin.time.Instant

/**
 * Changes the seats and intermediate stops of a published trip. Origin, destination, time and
 * vehicle stay fixed: passengers booked against them.
 *
 * Only the driver may edit, only while the trip is active and has not left. The seats can't drop
 * below those already confirmed nor exceed the vehicle, and a stop can't be removed while an open
 * booking meets the trip there (matched by name, as the booking stores it).
 */
class UpdateTripUseCase(
    private val tripRepository: TripRepository,
    private val bookingRepository: BookingRepository,
    private val vehicleRepository: VehicleRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        tripId: String,
        seatCount: Int,
        waypoints: List<Place>,
        now: Instant,
    ): Result<Unit> {
        val driverId = authRepository.getCurrentUserId()
            ?: return Result.failure(AppException.TripException.NotAuthenticated)
        val trip = tripRepository.getTripById(tripId).getOrElse {
            return Result.failure(AppException.TripException.Unknown)
        }
        if (trip.driverId != driverId || trip.status != TripStatus.Active ||
            trip.departureTime <= now.toEpochMilliseconds()
        ) {
            return Result.failure(AppException.TripException.NotEditable)
        }

        val vehicle = vehicleRepository.getVehicleById(trip.vehicleId).getOrNull()
            ?.takeIf { it.driverId == driverId }
            ?: return Result.failure(AppException.TripException.VehicleNotFound)
        if (seatCount < trip.confirmedSeats) {
            return Result.failure(AppException.TripException.SeatsBelowConfirmed(trip.confirmedSeats))
        }
        if (seatCount !in 1..vehicle.seatsAvailable) {
            return Result.failure(AppException.TripException.Invalid(listOf(TripValidationError.SeatsOutOfRange)))
        }

        val keptNames = waypoints.map { it.name }.toSet()
        val removedNames = trip.waypoints.map { it.name }.filter { it !in keptNames }.toSet()
        if (removedNames.isNotEmpty()) {
            val openBookings = try {
                bookingRepository.getOpenBookingsForTrip(tripId, driverId).first()
            } catch (_: Exception) {
                return Result.failure(AppException.TripException.Unknown)
            }
            val inUse = openBookings
                .filter { !it.status.isTerminal }
                .mapNotNull { it.meetingStop?.name }
                .filter { it in removedNames }
                .distinct()
            if (inUse.isNotEmpty()) return Result.failure(AppException.TripException.StopInUse(inUse))
        }

        return tripRepository.updateTripDetails(tripId, seatCount, waypoints)
    }
}
