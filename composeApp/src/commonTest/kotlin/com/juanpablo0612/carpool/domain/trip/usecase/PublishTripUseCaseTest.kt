package com.juanpablo0612.carpool.domain.trip.usecase

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripDriver
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.model.TripVehicle
import com.juanpablo0612.carpool.domain.trip.validation.TripDraft
import com.juanpablo0612.carpool.domain.trip.validation.TripValidationError
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

class PublishTripUseCaseTest {

    private val now = Instant.parse("2026-09-28T11:00:00Z")
    private val home = Place(name = "Casa", address = "", latitude = 6.17, longitude = -75.58)

    private fun draft(seats: Int = 3) = TripDraft(
        origin = home,
        destination = Place.EIA_LAS_PALMAS,
        departure = now + 1.days,
        vehicleCapacity = null,
        seatCount = seats,
        contributionPerPassenger = 0,
        message = "  Salgo puntual  ",
    )

    private fun useCase(
        uid: String? = "d1",
        trips: FakeTripRepository = FakeTripRepository(),
        routes: FakeRouteRepository = FakeRouteRepository(),
    ) = PublishTripUseCase(trips, routes, FakeVehicleRepository(listOf(vehicle())), FakeAuthRepository(uid))

    @Test
    fun publishesAOneOffTrip() = runTest {
        val trips = FakeTripRepository()
        val result = useCase(trips = trips)(draft(), "v1", now).getOrThrow()
        val trip = trips.created.single()
        assertEquals("", result.routeId)
        assertEquals("d1", trip.driverId)
        assertEquals(TripStatus.Active, trip.status)
        assertEquals(null, trip.contributionPerPassenger)
        assertEquals("Salgo puntual", trip.messageToPassengers)
    }

    @Test
    fun snapshotsTheDriverAndVehicleOntoTheTrip() = runTest {
        val trips = FakeTripRepository()
        useCase(trips = trips)(draft(), "v1", now).getOrThrow()
        val trip = trips.created.single()
        assertEquals(TripDriver(name = "Carolina Restrepo", photoUrl = "https://photo"), trip.driver)
        val car = vehicle()
        assertEquals(TripVehicle(brand = car.brand, model = car.model, color = car.color), trip.vehicle)
    }

    @Test
    fun requiresASignedInDriver() = runTest {
        assertEquals(AppException.TripException.NotAuthenticated, useCase(uid = null)(draft(), "v1", now).exceptionOrNull())
    }

    @Test
    fun rejectsAVehicleTheDriverDoesNotOwn() = runTest {
        assertEquals(AppException.TripException.VehicleNotFound, useCase(uid = "other")(draft(), "v1", now).exceptionOrNull())
    }

    @Test
    fun validatesSeatsAgainstTheActualVehicle() = runTest {
        val error = useCase()(draft(seats = 5), "v1", now).exceptionOrNull()
        assertIs<AppException.TripException.Invalid>(error)
        assertEquals(listOf(TripValidationError.SeatsOutOfRange), error.errors)
    }

    @Test
    fun savesTheRouteAndLinksTheTrip() = runTest {
        val trips = FakeTripRepository()
        val routes = FakeRouteRepository()
        val template = RouteTemplate("Ida a la U", setOf(DayOfWeek.MONDAY), LocalTime(7, 0))
        val result = useCase(trips = trips, routes = routes)(draft(), "v1", now, routeToSave = template).getOrThrow()
        assertEquals("route1", result.routeId)
        assertEquals("route1", trips.created.single().routeId)
        assertEquals("Ida a la U", routes.created.single().name)
    }

    @Test
    fun reportsTheSavedRouteWhenTheTripFails() = runTest {
        val template = RouteTemplate("Ida a la U", emptySet(), null)
        val error = useCase(trips = FakeTripRepository(failCreate = true))(draft(), "v1", now, routeToSave = template)
            .exceptionOrNull()
        assertEquals(AppException.TripException.RouteSavedTripFailed("route1"), error)
    }

    @Test
    fun anExistingRouteIsNotCreatedAgain() = runTest {
        val routes = FakeRouteRepository()
        val template = RouteTemplate("", emptySet(), null)
        val result = useCase(routes = routes)(draft(), "v1", now, existingRouteId = "r9", routeToSave = template).getOrThrow()
        assertEquals("r9", result.routeId)
        assertTrue(routes.created.isEmpty())
    }
}

class PublishRecurringTripsUseCaseTest {

    private val bogota = TimeZone.of("America/Bogota")
    private val now = LocalDateTime(2026, 9, 28, 6, 0).toInstant(bogota) // Monday
    private val route = Route(
        id = "r1",
        driverId = "d1",
        origin = Place(name = "Casa", address = "", latitude = 6.17, longitude = -75.58),
        destination = Place.EIA_LAS_PALMAS,
        waypoints = emptyList(),
        recurringDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
        typicalDepartureTime = LocalTime(7, 0),
    )
    private val settings = WeekTripSettings(vehicleId = "v1", seatCount = 3, contributionPerPassenger = 4_000, message = "")

    private fun useCase(trips: FakeTripRepository) = PublishRecurringTripsUseCase(
        trips,
        FakeVehicleRepository(listOf(vehicle())),
        FakeAuthRepository("d1"),
        GenerateRecurringTripSlotsUseCase(),
    )

    @Test
    fun publishesTheSelectedDaysInOneBatch() = runTest {
        val trips = FakeTripRepository()
        val dates = setOf(LocalDate(2026, 9, 28), LocalDate(2026, 9, 30))
        val ids = useCase(trips)(route, dates, settings, now, bogota).getOrThrow()
        assertEquals(2, ids.size)
        assertTrue(trips.created.all { it.routeId == "r1" && it.seatCount == 3 })
    }

    @Test
    fun skipsDaysThatAreAlreadyPublished() = runTest {
        val wednesday = LocalDateTime(2026, 9, 30, 7, 0).toInstant(bogota)
        val published = Trip(
            id = "t1",
            routeId = route.id,
            driverId = "d1",
            vehicleId = "v1",
            origin = route.origin,
            destination = route.destination,
            waypoints = emptyList(),
            departureTime = wednesday.toEpochMilliseconds(),
            status = TripStatus.Active,
        )
        val trips = FakeTripRepository(existing = listOf(published))
        val dates = setOf(LocalDate(2026, 9, 28), LocalDate(2026, 9, 30))

        val ids = useCase(trips)(route, dates, settings, now, bogota).getOrThrow()

        assertEquals(1, ids.size)
        val createdDate = Instant.fromEpochMilliseconds(trips.created.single().departureTime).toLocalDateTime(bogota).date
        assertEquals(LocalDate(2026, 9, 28), createdDate)
    }
}
