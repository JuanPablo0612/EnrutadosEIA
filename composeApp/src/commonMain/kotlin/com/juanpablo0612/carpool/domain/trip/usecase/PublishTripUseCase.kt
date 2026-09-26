package com.juanpablo0612.carpool.domain.trip.usecase

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.auth.repository.AuthRepository
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.domain.route.repository.RouteRepository
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.repository.TripRepository
import com.juanpablo0612.carpool.domain.trip.validation.TripDraft
import com.juanpablo0612.carpool.domain.trip.validation.TripDraftValidator
import com.juanpablo0612.carpool.domain.trip.validation.TripValidationError
import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlin.time.Instant

/** Stops, days and time to save as a frequent route alongside a one-off trip. */
data class RouteTemplate(
    val name: String,
    val recurringDays: Set<DayOfWeek>,
    val typicalDepartureTime: LocalTime?,
)

data class PublishedTrip(val tripId: String, val routeId: String)

/**
 * Publishes a trip: validates it, checks the vehicle belongs to the driver and has the seats,
 * optionally saves its stops as a new frequent route, and creates the trip linked to that route
 * (or to [existingRouteId]).
 *
 * If the route is saved but the trip then fails, fails with
 * [AppException.TripException.RouteSavedTripFailed] carrying the new route id, so a retry can pass
 * it as [existingRouteId] instead of creating the route twice.
 */
class PublishTripUseCase(
    private val tripRepository: TripRepository,
    private val routeRepository: RouteRepository,
    private val vehicleRepository: VehicleRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        draft: TripDraft,
        vehicleId: String,
        now: Instant,
        existingRouteId: String? = null,
        routeToSave: RouteTemplate? = null,
    ): Result<PublishedTrip> {
        val driverId = authRepository.getCurrentUserId()
            ?: return Result.failure(AppException.TripException.NotAuthenticated)

        val vehicle = vehicleRepository.getVehicleById(vehicleId).getOrNull()
            ?.takeIf { it.driverId == driverId }
            ?: return Result.failure(AppException.TripException.VehicleNotFound)

        val checked = draft.copy(
            vehicleCapacity = vehicle.seatsAvailable,
            routeNameToSave = routeToSave?.name.takeIf { existingRouteId == null },
        )
        val errors = TripDraftValidator.validate(checked, now)
        if (errors.isNotEmpty()) return Result.failure(AppException.TripException.Invalid(errors))
        val origin = checked.origin ?: return invalid(TripValidationError.OriginDestinationRequired)
        val destination = checked.destination ?: return invalid(TripValidationError.OriginDestinationRequired)

        val newRouteId = if (existingRouteId == null && routeToSave != null) {
            routeRepository.createRoute(
                Route(
                    driverId = driverId,
                    origin = origin,
                    destination = destination,
                    waypoints = checked.waypoints,
                    name = routeToSave.name.trim(),
                    recurringDays = routeToSave.recurringDays,
                    typicalDepartureTime = routeToSave.typicalDepartureTime,
                )
            ).getOrElse { return Result.failure(AppException.TripException.Unknown) }
        } else null
        val routeId = existingRouteId ?: newRouteId.orEmpty()

        val trip = Trip(
            routeId = routeId,
            driverId = driverId,
            vehicleId = vehicle.id,
            origin = origin,
            destination = destination,
            waypoints = checked.waypoints,
            departureTime = checked.departure.toEpochMilliseconds(),
            seatCount = checked.seatCount,
            contributionPerPassenger = checked.contributionPerPassenger?.takeIf { it > 0 },
            messageToPassengers = checked.message.trim(),
            status = TripStatus.Active,
        )
        return tripRepository.createTrip(trip).fold(
            onSuccess = { Result.success(PublishedTrip(it, routeId)) },
            onFailure = {
                Result.failure(
                    if (newRouteId != null) AppException.TripException.RouteSavedTripFailed(newRouteId)
                    else AppException.TripException.Unknown
                )
            },
        )
    }

    private fun invalid(error: TripValidationError): Result<PublishedTrip> =
        Result.failure(AppException.TripException.Invalid(listOf(error)))
}
