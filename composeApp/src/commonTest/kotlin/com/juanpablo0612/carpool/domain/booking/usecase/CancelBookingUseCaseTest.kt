package com.juanpablo0612.carpool.domain.booking.usecase

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CancelBookingUseCaseTest {

    private fun booking(status: BookingStatus = BookingStatus.Confirmed) = Booking(
        id = "b1",
        tripId = "t1",
        passengerId = "p1",
        driverId = "d1",
        passengerName = "",
        passengerEmail = "",
        originName = "",
        destinationName = "",
        departureTime = 1_000L,
        status = status,
        createdAt = 0L,
    )

    @Test
    fun anOpenBookingBeforeDepartureIsCancelled() = runTest {
        listOf(BookingStatus.Pending, BookingStatus.Confirmed).forEach { status ->
            val bookings = FakeBookingRepository()
            CancelBookingUseCase(bookings)(booking(status), now = 999L).getOrThrow()
            assertEquals(listOf<Pair<String, BookingStatus>>("b1" to BookingStatus.Cancelled), bookings.statusChanges)
        }
    }

    @Test
    fun aBookingIsNotCancelledOnceTheTripHasLeft() = runTest {
        val bookings = FakeBookingRepository()
        val result = CancelBookingUseCase(bookings)(booking(), now = 1_000L)
        assertEquals(AppException.BookingException.TripClosed, result.exceptionOrNull())
        assertTrue(bookings.statusChanges.isEmpty())
    }

    @Test
    fun aClosedBookingIsNotCancelledAgain() = runTest {
        listOf(BookingStatus.Rejected, BookingStatus.Cancelled).forEach { status ->
            val bookings = FakeBookingRepository()
            val result = CancelBookingUseCase(bookings)(booking(status), now = 0L)
            assertEquals(AppException.BookingException.TripClosed, result.exceptionOrNull(), "$status")
            assertTrue(bookings.statusChanges.isEmpty())
        }
    }
}
