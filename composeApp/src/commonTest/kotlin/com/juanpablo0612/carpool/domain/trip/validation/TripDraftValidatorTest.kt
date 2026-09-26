package com.juanpablo0612.carpool.domain.trip.validation

import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.validation.TripValidationError.ContributionOutOfRange
import com.juanpablo0612.carpool.domain.trip.validation.TripValidationError.DepartureTooFar
import com.juanpablo0612.carpool.domain.trip.validation.TripValidationError.DepartureTooSoon
import com.juanpablo0612.carpool.domain.trip.validation.TripValidationError.MessageTooLong
import com.juanpablo0612.carpool.domain.trip.validation.TripValidationError.NoVehicleSelected
import com.juanpablo0612.carpool.domain.trip.validation.TripValidationError.OriginDestinationRequired
import com.juanpablo0612.carpool.domain.trip.validation.TripValidationError.RouteNameRequired
import com.juanpablo0612.carpool.domain.trip.validation.TripValidationError.SameOriginDestination
import com.juanpablo0612.carpool.domain.trip.validation.TripValidationError.SeatsOutOfRange
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class TripDraftValidatorTest {

    private val now = Instant.parse("2026-09-28T11:00:00Z")
    private val home = Place(name = "Casa", address = "", latitude = 6.17, longitude = -75.58)

    private fun draft(
        origin: Place? = home,
        destination: Place? = Place.EIA_LAS_PALMAS,
        departure: Instant = now + 2.days,
        capacity: Int? = 4,
        seats: Int = 3,
        contribution: Int? = 5_000,
        message: String = "",
        routeName: String? = null,
    ) = TripDraft(
        origin = origin,
        destination = destination,
        departure = departure,
        vehicleCapacity = capacity,
        seatCount = seats,
        contributionPerPassenger = contribution,
        message = message,
        routeNameToSave = routeName,
    )

    private fun errors(d: TripDraft) = TripDraftValidator.validate(d, now)

    @Test
    fun aCompleteDraftIsValid() {
        assertTrue(errors(draft()).isEmpty())
    }

    @Test
    fun requiresOriginAndDestination() {
        assertEquals(listOf(OriginDestinationRequired), errors(draft(origin = null)))
        assertEquals(listOf(OriginDestinationRequired), errors(draft(destination = null)))
    }

    @Test
    fun rejectsTheSamePlaceAtBothEnds() {
        assertEquals(listOf(SameOriginDestination), errors(draft(destination = home.copy(latitude = 6.1701))))
    }

    @Test
    fun departureMustBeAtLeastFifteenMinutesAhead() {
        assertEquals(listOf(DepartureTooSoon), errors(draft(departure = now + 14.minutes)))
        assertTrue(errors(draft(departure = now + 16.minutes)).isEmpty())
    }

    @Test
    fun departureCannotBeMoreThanThirtyDaysAhead() {
        assertEquals(listOf(DepartureTooFar), errors(draft(departure = now + 31.days)))
    }

    @Test
    fun seatsMustFitTheVehicle() {
        assertEquals(listOf(NoVehicleSelected), errors(draft(capacity = null)))
        assertEquals(listOf(SeatsOutOfRange), errors(draft(seats = 0)))
        assertEquals(listOf(SeatsOutOfRange), errors(draft(seats = 5)))
    }

    @Test
    fun contributionIsBounded() {
        assertEquals(listOf(ContributionOutOfRange), errors(draft(contribution = -1)))
        assertEquals(
            listOf(ContributionOutOfRange),
            errors(draft(contribution = TripDraftValidator.MAX_CONTRIBUTION_COP + 1)),
        )
        assertTrue(errors(draft(contribution = null)).isEmpty())
    }

    @Test
    fun messageIsLimited() {
        assertEquals(listOf(MessageTooLong), errors(draft(message = "x".repeat(141))))
        assertTrue(errors(draft(message = "x".repeat(140))).isEmpty())
    }

    @Test
    fun savingAsRouteNeedsAName() {
        assertEquals(listOf(RouteNameRequired), errors(draft(routeName = " ")))
        assertTrue(errors(draft(routeName = "Ida a la U")).isEmpty())
    }
}
