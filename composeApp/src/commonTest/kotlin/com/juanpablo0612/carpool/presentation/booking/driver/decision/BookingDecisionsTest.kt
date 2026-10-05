package com.juanpablo0612.carpool.presentation.booking.driver.decision

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.domain.booking.model.RejectReason
import com.juanpablo0612.carpool.domain.booking.usecase.CancelBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.ConfirmBookingUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.FakeBookingRepository
import com.juanpablo0612.carpool.domain.booking.usecase.GetTripAvailableSeatsUseCase
import com.juanpablo0612.carpool.domain.booking.usecase.RejectBookingUseCase
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.trip.usecase.FakeTripRepository
import com.juanpablo0612.carpool.presentation.booking.BookingError
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BookingDecisionsTest {

    private val booking = Booking(
        id = "b1",
        tripId = "t1",
        passengerId = "p1",
        driverId = "d1",
        passengerName = "Andrea Mejía",
        passengerEmail = "",
        originName = "",
        destinationName = "",
        // Still ahead whenever the test runs, so it can be cancelled.
        departureTime = Long.MAX_VALUE,
        status = BookingStatus.Pending,
        createdAt = 0L,
    )

    private fun trip(confirmed: Int) = Trip(
        id = "t1",
        routeId = "",
        driverId = "d1",
        vehicleId = "v1",
        origin = Place.EIA_LAS_PALMAS,
        destination = Place.EIA_ZUNIGA,
        waypoints = emptyList(),
        departureTime = 1_000L,
        seatCount = 2,
        confirmedSeats = confirmed,
        status = TripStatus.Active,
    )

    private fun TestScope.decisions(bookings: FakeBookingRepository, confirmed: Int = 0) = BookingDecisions(
        confirmBooking = ConfirmBookingUseCase(
            bookings,
            GetTripAvailableSeatsUseCase(FakeTripRepository(existing = listOf(trip(confirmed)))),
        ),
        rejectBooking = RejectBookingUseCase(bookings),
        cancelBooking = CancelBookingUseCase(bookings),
        scope = this,
    )

    @Test
    fun aDoubleTapAcceptsOnce() = runTest {
        val bookings = FakeBookingRepository()
        val decisions = decisions(bookings)
        decisions.onAction(BookingDecisionAction.Accept(booking))
        decisions.onAction(BookingDecisionAction.Accept(booking))
        assertEquals(setOf("b1"), decisions.state.value.busyIds)
        advanceUntilIdle()
        assertEquals(listOf<Pair<String, BookingStatus>>("b1" to BookingStatus.Confirmed), bookings.statusChanges)
        assertTrue(decisions.state.value.busyIds.isEmpty())
    }

    @Test
    fun acceptingOnAFullTripSurfacesNoSeats() = runTest {
        val bookings = FakeBookingRepository()
        val decisions = decisions(bookings, confirmed = 2)
        decisions.onAction(BookingDecisionAction.Accept(booking))
        advanceUntilIdle()
        assertEquals(BookingError.NoSeatsAvailable, decisions.state.value.error)
        assertTrue(bookings.statusChanges.isEmpty())
    }

    @Test
    fun rejectingWaitsForAReasonAndSendsATrimmedComment() = runTest {
        val bookings = FakeBookingRepository()
        val decisions = decisions(bookings)
        decisions.onAction(BookingDecisionAction.Reject(booking))
        decisions.onAction(BookingDecisionAction.ConfirmReject)
        assertEquals(RejectionDraft(bookingId = "b1"), decisions.state.value.rejection)

        decisions.onAction(BookingDecisionAction.OnRejectReasonSelected(RejectReason.Other))
        decisions.onAction(BookingDecisionAction.OnRejectCommentChanged("  Ya salí  "))
        decisions.onAction(BookingDecisionAction.ConfirmReject)
        advanceUntilIdle()
        assertNull(decisions.state.value.rejection)
        assertEquals(listOf(Triple<String, RejectReason, String?>("b1", RejectReason.Other, "Ya salí")), bookings.rejections)
    }

    @Test
    fun aSuggestedReasonIsPreselected() = runTest {
        val decisions = decisions(FakeBookingRepository())
        decisions.onAction(BookingDecisionAction.Reject(booking, suggestedReason = RejectReason.TripFull))
        assertEquals(RejectReason.TripFull, decisions.state.value.rejection?.reason)
    }

    @Test
    fun cancellingAsksFirst() = runTest {
        val bookings = FakeBookingRepository()
        val decisions = decisions(bookings)
        decisions.onAction(BookingDecisionAction.Cancel(booking))
        assertEquals(booking, decisions.state.value.cancelling)
        assertTrue(bookings.statusChanges.isEmpty())

        decisions.onAction(BookingDecisionAction.ConfirmCancel)
        advanceUntilIdle()
        assertNull(decisions.state.value.cancelling)
        assertEquals(listOf<Pair<String, BookingStatus>>("b1" to BookingStatus.Cancelled), bookings.statusChanges)
    }

    @Test
    fun aFailedWriteSurfacesAnError() = runTest {
        val decisions = decisions(FakeBookingRepository(failWrites = true))
        decisions.onAction(BookingDecisionAction.Cancel(booking))
        decisions.onAction(BookingDecisionAction.ConfirmCancel)
        advanceUntilIdle()
        assertEquals(BookingError.Unknown, decisions.state.value.error)
    }
}
