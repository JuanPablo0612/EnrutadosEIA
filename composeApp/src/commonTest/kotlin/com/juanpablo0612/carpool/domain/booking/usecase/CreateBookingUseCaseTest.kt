package com.juanpablo0612.carpool.domain.booking.usecase

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.booking.model.BookingMeetingStop
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripDriver
import com.juanpablo0612.carpool.domain.trip.model.TripMeetingStop
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.usecase.FakeAuthRepository
import com.juanpablo0612.carpool.domain.trip.usecase.FakeTripRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CreateBookingUseCaseTest {

    private val park = Place(name = "Parque de Envigado", address = "", latitude = 6.17, longitude = -75.58)

    /** Before [trip]'s departure. */
    private val now = 5L

    private fun trip(confirmed: Int = 0, status: TripStatus = TripStatus.Active) = Trip(
        id = "t1",
        routeId = "",
        driverId = "d1",
        vehicleId = "v1",
        driver = TripDriver(name = "Santiago Vélez", photoUrl = "https://driver"),
        origin = Place(name = "Viva Envigado", address = "", latitude = 6.17, longitude = -75.59),
        destination = Place.EIA_LAS_PALMAS,
        waypoints = listOf(park),
        departureTime = 1_000L,
        seatCount = 3,
        confirmedSeats = confirmed,
        status = status,
    )

    private fun useCase(bookings: FakeBookingRepository, trip: Trip = trip()) = CreateBookingUseCase(
        bookingRepository = bookings,
        authRepository = FakeAuthRepository("p1"),
        getTripAvailableSeatsUseCase = GetTripAvailableSeatsUseCase(FakeTripRepository(existing = listOf(trip))),
    )

    @Test
    fun recordsWhatBothListsShow() = runTest {
        val bookings = FakeBookingRepository()
        useCase(bookings)(trip(), TripMeetingStop(pathIndex = 1, isDropoff = false), "  Llevo maleta  ", now = now)
            .getOrThrow()
        val booking = bookings.created.single()
        assertEquals("p1", booking.passengerId)
        assertEquals("d1", booking.driverId)
        assertEquals(TripDriver(name = "Santiago Vélez", photoUrl = "https://driver"), booking.driver)
        assertEquals("https://photo", booking.passengerPhotoUrl)
        assertEquals(BookingMeetingStop(name = "Parque de Envigado", isDropoff = false), booking.meetingStop)
        assertEquals("Llevo maleta", booking.passengerMessage)
        assertEquals(BookingStatus.Pending, booking.status)
        assertEquals(now, booking.createdAt)
    }

    @Test
    fun anOutOfRangeMeetingStopIsDropped() = runTest {
        val bookings = FakeBookingRepository()
        useCase(bookings)(trip(), TripMeetingStop(pathIndex = 9, isDropoff = true), now = now).getOrThrow()
        assertNull(bookings.created.single().meetingStop)
    }

    @Test
    fun aBlankMessageIsStoredAsNone() = runTest {
        val bookings = FakeBookingRepository()
        useCase(bookings)(trip(), passengerMessage = "   ", now = now).getOrThrow()
        assertNull(bookings.created.single().passengerMessage)
    }

    @Test
    fun aFullTripIsRefused() = runTest {
        val bookings = FakeBookingRepository()
        val full = trip(confirmed = 3)
        val result = useCase(bookings, trip = full)(full, now = now)
        assertEquals(AppException.BookingException.NoSeatsAvailable, result.exceptionOrNull())
        assertTrue(bookings.created.isEmpty())
    }

    @Test
    fun aSecondRequestIsRefused() = runTest {
        val bookings = FakeBookingRepository(hasActive = true)
        assertEquals(AppException.BookingException.AlreadyBooked, useCase(bookings)(trip(), now = now).exceptionOrNull())
    }

    @Test
    fun aDepartedTripIsRefused() = runTest {
        val bookings = FakeBookingRepository()
        val result = useCase(bookings)(trip(), now = 1_000L)
        assertEquals(AppException.BookingException.TripClosed, result.exceptionOrNull())
        assertTrue(bookings.created.isEmpty())
    }

    @Test
    fun aTripThatIsNoLongerActiveIsRefused() = runTest {
        listOf(TripStatus.InProgress, TripStatus.Completed, TripStatus.Cancelled).forEach { status ->
            val bookings = FakeBookingRepository()
            val closed = trip(status = status)
            val result = useCase(bookings, trip = closed)(closed, now = now)
            assertEquals(AppException.BookingException.TripClosed, result.exceptionOrNull(), "$status")
            assertTrue(bookings.created.isEmpty())
        }
    }
}
