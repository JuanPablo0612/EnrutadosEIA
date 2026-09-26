package com.juanpablo0612.carpool.domain.trip.usecase

import com.juanpablo0612.carpool.domain.trip.model.RecurringTripSlot
import com.juanpablo0612.carpool.domain.trip.model.SlotStatus
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

/**
 * Lists the days in the next [DEFAULT_WINDOW_DAYS] (rolling, starting today) that a recurring
 * route runs on, and whether each can be published.
 *
 * A day is already published if the driver has a live (active or in-progress) trip on the same
 * route that calendar day, at any time, so changing the route's time afterwards never creates a
 * second trip that day. A day conflicts if another live trip departs within the conflict window.
 * `now` and the time zone are parameters so the logic is deterministic and testable.
 */
class GenerateRecurringTripSlotsUseCase {

    operator fun invoke(
        routeId: String,
        recurringDays: Set<DayOfWeek>,
        departureTime: LocalTime,
        driverTrips: List<Trip>,
        now: Instant,
        timeZone: TimeZone,
        windowDays: Int = DEFAULT_WINDOW_DAYS,
        minLeadTime: Duration = MIN_LEAD_TIME,
        conflictWindow: Duration = CONFLICT_WINDOW,
    ): List<RecurringTripSlot> {
        require(routeId.isNotBlank()) { "A recurring slot needs a saved route" }
        require(windowDays in 1..14) { "windowDays must be 1..14" }
        if (recurringDays.isEmpty()) return emptyList()

        val today = now.toLocalDateTime(timeZone).date
        val liveTrips = driverTrips.filter { it.status is TripStatus.Active || it.status is TripStatus.InProgress }

        return (0 until windowDays)
            .map { today.plus(it, DateTimeUnit.DAY) }
            .filter { it.dayOfWeek in recurringDays }
            .map { date ->
                val departure = LocalDateTime(date, departureTime).toInstant(timeZone)
                RecurringTripSlot(date, departure, statusOf(date, departure, routeId, liveTrips, now, timeZone, minLeadTime, conflictWindow))
            }
            .sortedBy { it.departure }
    }

    /**
     * The date a trip on this route would most naturally be published for: the first publishable
     * recurring day in the window, else today if [departureTime] is still ahead, else tomorrow.
     */
    fun nextOccurrence(
        routeId: String,
        recurringDays: Set<DayOfWeek>,
        departureTime: LocalTime,
        driverTrips: List<Trip>,
        now: Instant,
        timeZone: TimeZone,
    ): LocalDate {
        invoke(routeId, recurringDays, departureTime, driverTrips, now, timeZone)
            .firstOrNull { it.status.isPublishable }
            ?.let { return it.date }
        val today = now.toLocalDateTime(timeZone).date
        val todayDeparture = LocalDateTime(today, departureTime).toInstant(timeZone)
        return if (todayDeparture >= now + MIN_LEAD_TIME) today else today.plus(1, DateTimeUnit.DAY)
    }

    private fun statusOf(
        date: LocalDate,
        departure: Instant,
        routeId: String,
        liveTrips: List<Trip>,
        now: Instant,
        timeZone: TimeZone,
        minLeadTime: Duration,
        conflictWindow: Duration,
    ): SlotStatus {
        if (departure < now + minLeadTime) return SlotStatus.Passed

        liveTrips.firstOrNull { it.routeId == routeId && it.localDate(timeZone) == date }
            ?.let { return SlotStatus.AlreadyPublished(it.id) }

        liveTrips.firstOrNull { abs(it.departureTime - departure.toEpochMilliseconds()) < conflictWindow.inWholeMilliseconds }
            ?.let { return SlotStatus.Conflict(it.id, Instant.fromEpochMilliseconds(it.departureTime)) }

        return SlotStatus.Available
    }

    private fun Trip.localDate(timeZone: TimeZone): LocalDate =
        Instant.fromEpochMilliseconds(departureTime).toLocalDateTime(timeZone).date

    companion object {
        const val DEFAULT_WINDOW_DAYS = 7
        val MIN_LEAD_TIME = 15.minutes
        val CONFLICT_WINDOW = 60.minutes
    }
}
