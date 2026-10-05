package com.juanpablo0612.carpool.domain.trip.usecase

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingMeetingStop
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.usecase.FakeBookingRepository
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.validation.TripValidationError
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

class UpdateTripUseCaseTest {

    private val now = Instant.parse("2026-09-28T11:00:00Z")
    private val home = Place(name = "Casa", address = "", latitude = 6.17, longitude = -75.58)
    private val mall = Place(name = "Viva Envigado", address = "", latitude = 6.18, longitude = -75.59)
    private val park = Place(name = "Parque de Envigado", address = "", latitude = 6.17, longitude = -75.59)

    private fun trip(
        driverId: String = "d1",
        status: TripStatus = TripStatus.Active,
        departure: Instant = now + 2.hours,
        confirmed: Int = 0,
    ) = Trip(
        id = "t1",
        routeId = "",
        driverId = driverId,
        vehicleId = "v1",
        origin = home,
        destination = Place.EIA_LAS_PALMAS,
        waypoints = listOf(mall, park),
        departureTime = departure.toEpochMilliseconds(),
        seatCount = 3,
        status = status,
        confirmedSeats = confirmed,
    )

    private fun booking(stop: String, status: BookingStatus) = Booking(
        id = "b-$stop-$status",
        tripId = "t1",
        passengerId = "p1",
        driverId = "d1",
        passengerName = "Ana",
        passengerEmail = "ana@eia.edu.co",
        meetingStop = BookingMeetingStop(name = stop, isDropoff = false),
        originName = home.name,
        destinationName = Place.EIA_LAS_PALMAS.name,
        departureTime = (now + 2.hours).toEpochMilliseconds(),
        status = status,
        createdAt = 0,
    )

    private class Setup(
        val trips: FakeTripRepository,
        val useCase: UpdateTripUseCase,
    )

    private fun setup(
        uid: String? = "d1",
        trip: Trip = trip(),
        bookings: List<Booking> = emptyList(),
    ): Setup {
        val trips = FakeTripRepository(existing = listOf(trip))
        val useCase = UpdateTripUseCase(
            trips,
            FakeBookingRepository(openBookings = bookings),
            FakeVehicleRepository(listOf(vehicle(seats = 4))),
            FakeAuthRepository(uid),
        )
        return Setup(trips, useCase)
    }

    @Test
    fun updatesSeatsAndStops() = runTest {
        val s = setup()
        s.useCase("t1", 4, listOf(mall), now).getOrThrow()
        assertEquals(Triple("t1", 4, listOf(mall)), s.trips.detailUpdates.single())
    }

    @Test
    fun requiresASignedInDriver() = runTest {
        val s = setup(uid = null)
        assertEquals(AppException.TripException.NotAuthenticated, s.useCase("t1", 2, listOf(mall, park), now).exceptionOrNull())
        assertTrue(s.trips.detailUpdates.isEmpty())
    }

    @Test
    fun rejectsAnotherDriversTrip() = runTest {
        val s = setup(uid = "other")
        assertEquals(AppException.TripException.NotEditable, s.useCase("t1", 2, listOf(mall, park), now).exceptionOrNull())
    }

    @Test
    fun rejectsATripThatIsNotActive() = runTest {
        val s = setup(trip = trip(status = TripStatus.Cancelled))
        assertEquals(AppException.TripException.NotEditable, s.useCase("t1", 2, listOf(mall, park), now).exceptionOrNull())
    }

    @Test
    fun rejectsATripThatAlreadyLeft() = runTest {
        val s = setup(trip = trip(departure = now))
        assertEquals(AppException.TripException.NotEditable, s.useCase("t1", 2, listOf(mall, park), now).exceptionOrNull())
    }

    @Test
    fun rejectsFewerSeatsThanConfirmed() = runTest {
        val s = setup(trip = trip(confirmed = 3))
        assertEquals(AppException.TripException.SeatsBelowConfirmed(3), s.useCase("t1", 2, listOf(mall, park), now).exceptionOrNull())
        assertTrue(s.trips.detailUpdates.isEmpty())
    }

    @Test
    fun allowsExactlyTheConfirmedSeats() = runTest {
        val s = setup(trip = trip(confirmed = 3))
        assertTrue(s.useCase("t1", 3, listOf(mall, park), now).isSuccess)
    }

    @Test
    fun rejectsMoreSeatsThanTheVehicleHas() = runTest {
        val s = setup()
        assertEquals(
            AppException.TripException.Invalid(listOf(TripValidationError.SeatsOutOfRange)),
            s.useCase("t1", 5, listOf(mall, park), now).exceptionOrNull(),
        )
    }

    @Test
    fun rejectsZeroSeats() = runTest {
        val s = setup()
        assertEquals(
            AppException.TripException.Invalid(listOf(TripValidationError.SeatsOutOfRange)),
            s.useCase("t1", 0, listOf(mall, park), now).exceptionOrNull(),
        )
    }

    @Test
    fun rejectsRemovingAStopAPendingBookingMeetsAt() = runTest {
        val s = setup(bookings = listOf(booking(park.name, BookingStatus.Pending)))
        assertEquals(
            AppException.TripException.StopInUse(listOf(park.name)),
            s.useCase("t1", 3, listOf(mall), now).exceptionOrNull(),
        )
        assertTrue(s.trips.detailUpdates.isEmpty())
    }

    @Test
    fun allowsRemovingAStopOnlyACancelledBookingUsed() = runTest {
        val s = setup(bookings = listOf(booking(park.name, BookingStatus.Cancelled)))
        assertTrue(s.useCase("t1", 3, listOf(mall), now).isSuccess)
    }

    @Test
    fun allowsAddingAndReorderingStopsInUse() = runTest {
        val s = setup(bookings = listOf(booking(park.name, BookingStatus.Confirmed)))
        val stops = listOf(park, home.copy(name = "Nuevo"), mall)
        s.useCase("t1", 3, stops, now).getOrThrow()
        assertEquals(stops, s.trips.detailUpdates.single().third)
    }
}
