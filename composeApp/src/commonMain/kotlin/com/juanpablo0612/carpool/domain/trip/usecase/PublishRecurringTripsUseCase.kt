package com.juanpablo0612.carpool.domain.trip.usecase

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripDriver
import com.juanpablo0612.carpool.domain.trip.model.TripVehicle
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import com.juanpablo0612.carpool.domain.trip.validation.TripDraft
import com.juanpablo0612.carpool.domain.trip.validation.TripDraftValidator
import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

/** Seats, contribution and message shared by every trip of a published week. */
data class WeekTripSettings(
    val vehicleId: String,
    val seatCount: Int,
    val contributionPerPassenger: Int?,
    val message: String,
)

/**
 * Publishes a recurring route on the selected [dates] in one atomic batch. Slots are recomputed
 * against the driver's current trips first, so a double tap or a second device never publishes
 * the same day twice; dates that stopped being publishable are skipped.
 */
class PublishRecurringTripsUseCase(
    private val tripRepository: TripRepository,
    private val vehicleRepository: VehicleRepository,
    private val authRepository: AuthRepository,
    private val generateSlots: GenerateRecurringTripSlotsUseCase,
) {
    suspend operator fun invoke(
        route: Route,
        dates: Set<LocalDate>,
        settings: WeekTripSettings,
        now: Instant,
        timeZone: TimeZone,
    ): Result<List<String>> {
        val driverId = authRepository.getCurrentUserId()
            ?: return Result.failure(AppException.TripException.NotAuthenticated)
        val departureTime = route.typicalDepartureTime
            ?: return Result.failure(AppException.TripException.Unknown)
        val vehicle = vehicleRepository.getVehicleById(settings.vehicleId).getOrNull()
            ?.takeIf { it.driverId == driverId }
            ?: return Result.failure(AppException.TripException.VehicleNotFound)
        // One read per publish, so each trip can carry the driver's name and photo and nobody
        // browsing trips has to read the profile.
        val driver = authRepository.getCurrentUser().getOrElse {
            return Result.failure(AppException.TripException.Unknown)
        }

        val driverTrips = runCatching { tripRepository.getDriverTrips(driverId).first() }
            .getOrElse { return Result.failure(AppException.TripException.Unknown) }
        val slots = generateSlots(route.id, route.recurringDays, departureTime, driverTrips, now, timeZone)
            .filter { it.date in dates && it.status.isPublishable }
        if (slots.isEmpty()) return Result.success(emptyList())

        val trips = slots.map { slot ->
            val draft = TripDraft(
                origin = route.origin,
                destination = route.destination,
                waypoints = route.waypoints,
                departure = slot.departure,
                vehicleCapacity = vehicle.seatsAvailable,
                seatCount = settings.seatCount,
                contributionPerPassenger = settings.contributionPerPassenger,
                message = settings.message,
            )
            val errors = TripDraftValidator.validate(draft, now)
            if (errors.isNotEmpty()) return Result.failure(AppException.TripException.Invalid(errors))
            Trip(
                routeId = route.id,
                driverId = driverId,
                vehicleId = vehicle.id,
            driver = TripDriver.from(driver),
            vehicle = TripVehicle.from(vehicle),
                origin = route.origin,
                destination = route.destination,
                waypoints = route.waypoints,
                departureTime = slot.departure.toEpochMilliseconds(),
                seatCount = settings.seatCount,
                contributionPerPassenger = settings.contributionPerPassenger?.takeIf { it > 0 },
                messageToPassengers = settings.message.trim(),
                status = TripStatus.Active,
            )
        }
        return tripRepository.createTrips(trips)
    }
}
