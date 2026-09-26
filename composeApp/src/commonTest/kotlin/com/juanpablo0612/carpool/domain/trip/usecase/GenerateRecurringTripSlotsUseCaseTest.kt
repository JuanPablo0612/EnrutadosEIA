package com.juanpablo0612.carpool.domain.trip.usecase

import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.SlotStatus
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Instant

class GenerateRecurringTripSlotsUseCaseTest {

    private val useCase = GenerateRecurringTripSlotsUseCase()
    private val bogota = TimeZone.of("America/Bogota")
    private val seven = LocalTime(7, 0)
    private val mwf = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)

    // Monday 2026-09-28.
    private fun at(day: Int, hour: Int, minute: Int = 0, month: Int = 9): Instant =
        LocalDateTime(2026, month, day, hour, minute).toInstant(bogota)

    private fun trip(
        id: String,
        departure: Instant,
        routeId: String = "r1",
        status: TripStatus = TripStatus.Active,
    ) = Trip(
        id = id,
        routeId = routeId,
        driverId = "d",
        vehicleId = "v",
        origin = Place(name = "A", address = "", latitude = 6.1, longitude = -75.5),
        destination = Place.EIA_LAS_PALMAS,
        waypoints = emptyList(),
        departureTime = departure.toEpochMilliseconds(),
        status = status,
    )

    private fun slots(now: Instant, trips: List<Trip> = emptyList(), days: Set<DayOfWeek> = mwf, time: LocalTime = seven) =
        useCase("r1", days, time, trips, now, bogota)

    @Test
    fun noDaysMeansNoSlots() {
        assertTrue(slots(at(28, 6), days = emptySet()).isEmpty())
    }

    @Test
    fun listsMatchingDaysAcrossAMonthBoundary() {
        val result = slots(at(28, 6))
        assertEquals(
            listOf(LocalDate(2026, 9, 28), LocalDate(2026, 9, 30), LocalDate(2026, 10, 2)),
            result.map { it.date },
        )
        assertTrue(result.all { it.status == SlotStatus.Available })
    }

    @Test
    fun windowIsSevenDaysStartingToday() {
        val result = slots(at(28, 7, 30))
        assertEquals(SlotStatus.Passed, result.first().status)
        assertTrue(result.none { it.date == LocalDate(2026, 10, 5) })
    }

    @Test
    fun departuresWithinTheLeadTimeHavePassed() {
        assertEquals(SlotStatus.Passed, slots(at(28, 6, 50)).first().status)
        assertEquals(SlotStatus.Available, slots(at(28, 6, 44)).first().status)
    }

    @Test
    fun sameRouteSameDayIsAlreadyPublishedEvenAtAnotherTime() {
        val result = slots(at(28, 6), listOf(trip("t1", at(30, 7, 30))))
        val wednesday = result.single { it.date == LocalDate(2026, 9, 30) }
        assertEquals(SlotStatus.AlreadyPublished("t1"), wednesday.status)
    }

    @Test
    fun cancelledTripsDoNotBlock() {
        val result = slots(at(28, 6), listOf(trip("t1", at(30, 7), status = TripStatus.Cancelled)))
        assertEquals(SlotStatus.Available, result.single { it.date == LocalDate(2026, 9, 30) }.status)
    }

    @Test
    fun otherTripsCloseInTimeConflict() {
        val near = slots(at(28, 6), listOf(trip("t2", at(2, 7, 20, month = 10), routeId = "r2")))
        assertIs<SlotStatus.Conflict>(near.single { it.date == LocalDate(2026, 10, 2) }.status)

        val far = slots(at(28, 6), listOf(trip("t2", at(2, 8, 30, month = 10), routeId = "r2")))
        assertEquals(SlotStatus.Available, far.single { it.date == LocalDate(2026, 10, 2) }.status)
    }

    @Test
    fun oneOffTripsConflictRatherThanCountAsPublished() {
        val result = slots(at(28, 6), listOf(trip("t3", at(2, 6, 30, month = 10), routeId = "")))
        assertIs<SlotStatus.Conflict>(result.single { it.date == LocalDate(2026, 10, 2) }.status)
    }

    @Test
    fun todayIsResolvedInTheLocalZoneNotUtc() {
        // 04:30 UTC on Monday is still Sunday 23:30 in Bogotá.
        val now = Instant.parse("2026-09-28T04:30:00Z")
        val result = useCase("r1", setOf(DayOfWeek.SUNDAY), LocalTime(23, 50), emptyList(), now, bogota)
        assertEquals(LocalDate(2026, 9, 27), result.first().date)
        assertEquals(SlotStatus.Available, result.first().status)
    }

    @Test
    fun lateNightDeparturesKeepTheirLocalDate() {
        val lateFriday = at(2, 23, 59, month = 10)
        val result = slots(at(28, 6), listOf(trip("t1", lateFriday)), days = setOf(DayOfWeek.FRIDAY), time = LocalTime(23, 59))
        assertEquals(SlotStatus.AlreadyPublished("t1"), result.single().status)
    }

    @Test
    fun everyDayGivesSevenSortedSlots() {
        val result = slots(at(28, 6), days = DayOfWeek.entries.toSet())
        assertEquals(7, result.size)
        assertEquals(result.sortedBy { it.departure }, result)
    }

    @Test
    fun oneOffRouteIdIsRejected() {
        assertFailsWith<IllegalArgumentException> { useCase("", mwf, seven, emptyList(), at(28, 6), bogota) }
    }

    @Test
    fun nextOccurrencePrefersFirstPublishableRecurringDay() {
        val published = listOf(trip("t1", at(28, 7)))
        assertEquals(LocalDate(2026, 9, 30), useCase.nextOccurrence("r1", mwf, seven, published, at(28, 6), bogota))
    }

    @Test
    fun nextOccurrenceFallsBackToTodayOrTomorrow() {
        assertEquals(LocalDate(2026, 9, 28), useCase.nextOccurrence("r1", emptySet(), seven, emptyList(), at(28, 6), bogota))
        assertEquals(LocalDate(2026, 9, 29), useCase.nextOccurrence("r1", emptySet(), seven, emptyList(), at(28, 8), bogota))
    }
}
