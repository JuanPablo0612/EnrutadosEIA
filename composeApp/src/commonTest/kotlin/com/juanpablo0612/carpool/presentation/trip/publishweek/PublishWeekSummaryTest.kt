package com.juanpablo0612.carpool.presentation.trip.publishweek

import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.domain.trip.model.RecurringTripSlot
import com.juanpablo0612.carpool.domain.trip.model.SlotStatus
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

class PublishWeekSummaryTest {

    private val zone = TimeZone.of("America/Bogota")
    private fun date(day: Int) = LocalDate(2026, 9, day)
    private fun at(day: Int, hour: Int = 6): Instant = LocalDateTime(2026, 9, day, hour, 45).toInstant(zone)
    private fun slot(day: Int, status: SlotStatus) = RecurringTripSlot(date(day), at(day), status)

    private val route = Route(
        id = "r1",
        driverId = "d1",
        origin = Place(name = "Casa", address = "", latitude = 6.17, longitude = -75.58),
        destination = Place.EIA_LAS_PALMAS,
        waypoints = emptyList(),
        name = "Ida a la U",
        recurringDays = setOf(DayOfWeek.MONDAY),
        typicalDepartureTime = LocalTime(6, 45),
    )
    private val vehicle = Vehicle(
        id = "v1", driverId = "d1", brand = "Mazda", model = "3", licensePlate = "ABC123",
        color = "Gris", year = 2020, seatsAvailable = 4,
    )

    @Test
    fun splitsSelectedPublishableDaysFromTheOnesLeftOut() {
        val state = PublishWeekUiState(
            isLoading = false,
            route = route,
            slots = listOf(
                slot(30, SlotStatus.Available),
                slot(25, SlotStatus.Available),
                slot(26, SlotStatus.Conflict("t1", at(26, 7))),
                slot(27, SlotStatus.Conflict("t2", at(27, 7))),
                slot(28, SlotStatus.AlreadyPublished("t3")),
                slot(24, SlotStatus.Passed),
                slot(29, SlotStatus.Available),
            ),
            // A stale tick on an already-published day must not count as a trip.
            selectedDates = setOf(date(25), date(26), date(28), date(30)),
            vehicles = listOf(vehicle),
            selectedVehicleId = "v1",
            seatCount = 3,
            contributionPerPassenger = 5_000,
            message = "Salgo puntual",
        )

        val summary = state.toSummary()!!

        assertEquals(listOf(date(25), date(26), date(30)), summary.trips.map { it.date })
        assertEquals(
            listOf(
                date(24) to SkipReason.Passed,
                date(27) to SkipReason.Conflict(at(27, 7)),
                date(28) to SkipReason.AlreadyPublished,
                date(29) to SkipReason.NotSelected,
            ),
            summary.skipped.map { it.slot.date to it.reason },
        )
        assertEquals(vehicle, summary.vehicle)
        assertEquals(3, summary.seatCount)
        assertEquals(5_000, summary.contributionPerPassenger)
        assertEquals("Salgo puntual", summary.message)
    }

    @Test
    fun hasNoSummaryWithoutARoute() {
        assertNull(PublishWeekUiState(isLoading = false).toSummary())
    }
}
