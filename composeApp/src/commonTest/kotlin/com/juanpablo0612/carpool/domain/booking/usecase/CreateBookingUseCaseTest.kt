package com.juanpablo0612.carpool.domain.booking.usecase

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingMeetingStop
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.model.RejectReason
import com.juanpablo0612.carpool.domain.booking.repository.BookingRepository
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripDriver
import com.juanpablo0612.carpool.domain.trip.model.TripMeetingStop
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.usecase.FakeAuthRepository
import com.juanpablo0612.carpool.domain.trip.usecase.FakeTripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CreateBookingUseCaseTest {

    private val park = Place(name = "Parque de Envigado", address = "", latitude = 6.17, longitude = -75.58)

    private fun trip(confirmed: Int = 0) = Trip(
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
        status = TripStatus.Active,
    )

    private fun useCase(bookings: FakeBookingRepository, trip: Trip = trip()) = CreateBookingUseCase(
        bookingRepository = bookings,
        authRepository = FakeAuthRepository("p1"),
        getTripAvailableSeatsUseCase = GetTripAvailableSeatsUseCase(FakeTripRepository(existing = listOf(trip))),
    )

    @Test
    fun recordsWhatBothListsShow() = runTest {
        val bookings = FakeBookingRepository()
        useCase(bookings)(trip(), TripMeetingStop(pathIndex = 1, isDropoff = false), "  Llevo maleta  ", now = 5L)
            .getOrThrow()
        val booking = bookings.created.single()
        assertEquals("p1", booking.passengerId)
        assertEquals("d1", booking.driverId)
        assertEquals(TripDriver(name = "Santiago Vélez", photoUrl = "https://driver"), booking.driver)
        assertEquals("https://photo", booking.passengerPhotoUrl)
        assertEquals(BookingMeetingStop(name = "Parque de Envigado", isDropoff = false), booking.meetingStop)
        assertEquals("Llevo maleta", booking.passengerMessage)
        assertEquals(BookingStatus.Pending, booking.status)
        assertEquals(5L, booking.createdAt)
    }

    @Test
    fun anOutOfRangeMeetingStopIsDropped() = runTest {
        val bookings = FakeBookingRepository()
        useCase(bookings)(trip(), TripMeetingStop(pathIndex = 9, isDropoff = true)).getOrThrow()
        assertNull(bookings.created.single().meetingStop)
    }

    @Test
    fun aBlankMessageIsStoredAsNone() = runTest {
        val bookings = FakeBookingRepository()
        useCase(bookings)(trip(), passengerMessage = "   ").getOrThrow()
        assertNull(bookings.created.single().passengerMessage)
    }

    @Test
    fun aFullTripIsRefused() = runTest {
        val bookings = FakeBookingRepository()
        val full = trip(confirmed = 3)
        val result = useCase(bookings, trip = full)(full)
        assertEquals(AppException.BookingException.NoSeatsAvailable, result.exceptionOrNull())
        assertTrue(bookings.created.isEmpty())
    }

    @Test
    fun aSecondRequestIsRefused() = runTest {
        val bookings = FakeBookingRepository(hasActive = true)
        assertEquals(AppException.BookingException.AlreadyBooked, useCase(bookings)(trip()).exceptionOrNull())
    }
}

private fun unused(): Nothing = error("not used by these tests")

private class FakeBookingRepository(private val hasActive: Boolean = false) : BookingRepository {
    val created = mutableListOf<Booking>()
    override suspend fun createBooking(booking: Booking): Result<Unit> {
        created += booking
        return Result.success(Unit)
    }
    override suspend fun hasActiveBooking(passengerId: String, tripId: String) = Result.success(hasActive)
    override fun getPassengerBookings(passengerId: String): Flow<List<Booking>> = unused()
    override fun getDriverBookingRequests(driverId: String): Flow<List<Booking>> = unused()
    override fun getAllDriverBookings(driverId: String): Flow<List<Booking>> = unused()
    override fun getOpenDriverBookings(driverId: String, departingAfter: Long): Flow<List<Booking>> = unused()
    override fun getOpenBookingsForTrip(tripId: String, driverId: String): Flow<List<Booking>> = unused()
    override fun getBookingsForTripAsDriver(tripId: String, driverId: String): Flow<List<Booking>> = unused()
    override fun getBookingsForTripAsPassenger(tripId: String, passengerId: String): Flow<List<Booking>> = unused()
    override suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit> = unused()
    override suspend fun rejectBookingWithReason(bookingId: String, reason: RejectReason, comment: String?): Result<Unit> = unused()
}
