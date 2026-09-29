package com.juanpablo0612.carpool.presentation.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.date_of_connector
import enrutadoseia.composeapp.generated.resources.date_today
import enrutadoseia.composeapp.generated.resources.date_tomorrow
import enrutadoseia.composeapp.generated.resources.day_names_short
import enrutadoseia.composeapp.generated.resources.month_names
import enrutadoseia.composeapp.generated.resources.relative_date_later
import enrutadoseia.composeapp.generated.resources.relative_date_this_week
import enrutadoseia.composeapp.generated.resources.relative_date_today
import enrutadoseia.composeapp.generated.resources.relative_date_tomorrow
import enrutadoseia.composeapp.generated.resources.relative_day_at_time
import enrutadoseia.composeapp.generated.resources.relative_in_minutes
import enrutadoseia.composeapp.generated.resources.relative_today
import enrutadoseia.composeapp.generated.resources.relative_tomorrow
import enrutadoseia.composeapp.generated.resources.time_am
import enrutadoseia.composeapp.generated.resources.time_pm
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.Instant

/** The current time frozen at first composition — shared by every screen that needs a stable
 *  "now" to compare list items against without re-reading the clock on each recomposition. */
@Composable
fun rememberNowMs(): Long = remember { Clock.System.now().toEpochMilliseconds() }

fun formatShortTime(hour: Int, minute: Int, amMarker: String, pmMarker: String): String {
    val h12 = if (hour % 12 == 0) 12 else hour % 12
    val amPm = if (hour < 12) amMarker else pmMarker
    val min = minute.toString().padStart(2, '0')
    return "$h12:$min $amPm"
}

private fun Int.twoDigits(): String = toString().padStart(2, '0')

/** Numeric day and month, "dd/MM". */
fun formatDayMonth(date: LocalDate): String =
    "${date.day.twoDigits()}/${date.month.number.twoDigits()}"

/** Full numeric date, "dd/MM/yyyy". */
fun formatNumericDate(date: LocalDate): String = "${formatDayMonth(date)}/${date.year}"

/** A departure instant in the device time zone as "dd/MM · h:mm AM". */
@Composable
fun formatDayMonthTime(epochMs: Long): String {
    val local = Instant.fromEpochMilliseconds(epochMs)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val time = formatShortTime(
        hour = local.hour,
        minute = local.minute,
        amMarker = stringResource(Res.string.time_am),
        pmMarker = stringResource(Res.string.time_pm),
    )
    return "${formatDayMonth(local.date)} · $time"
}

/** The clock time of [epochMs] in the device time zone, e.g. "6:30 p. m.". */
@Composable
fun formatTime(epochMs: Long): String {
    val local = Instant.fromEpochMilliseconds(epochMs).toLocalDateTime(TimeZone.currentSystemDefault())
    return formatShortTime(
        hour = local.hour,
        minute = local.minute,
        amMarker = stringResource(Res.string.time_am),
        pmMarker = stringResource(Res.string.time_pm),
    )
}

/**
 * The day of [epochMs] relative to [now]: "Hoy", "Mañana", or "vie. 3 de octubre" further out.
 * Pairs with [formatTime] wherever a departure is shown as a big time plus its day.
 */
@Composable
fun departureDayLabel(epochMs: Long, now: Long): String {
    val tz = TimeZone.currentSystemDefault()
    val date = Instant.fromEpochMilliseconds(epochMs).toLocalDateTime(tz).date
    val today = Instant.fromEpochMilliseconds(now).toLocalDateTime(tz).date
    return when (date) {
        today -> stringResource(Res.string.date_today)
        today.plus(1, DateTimeUnit.DAY) -> stringResource(Res.string.date_tomorrow)
        else -> formatLongDate(
            year = date.year,
            month = date.month.number,
            day = date.day,
            dayNames = stringArrayResource(Res.array.day_names_short),
            monthNames = stringArrayResource(Res.array.month_names),
            connector = stringResource(Res.string.date_of_connector),
        )
    }
}

/**
 * A departure time phrased relative to [now] — "In 20 min", "Today · 7:05 AM", "Tue. · 7:05 AM".
 *
 * Composable because every phrasing, the AM/PM markers and the weekday abbreviations all come from
 * `strings.xml`, so the result follows the device locale.
 */
@Composable
fun relativeTime(epochMs: Long, now: Long = Clock.System.now().toEpochMilliseconds()): String {
    val tz = TimeZone.currentSystemDefault()
    val departure = Instant.fromEpochMilliseconds(epochMs).toLocalDateTime(tz)
    val nowLocal = Instant.fromEpochMilliseconds(now).toLocalDateTime(tz)
    val diffMin = (epochMs - now) / 60_000

    val time = formatShortTime(
        hour = departure.hour,
        minute = departure.minute,
        amMarker = stringResource(Res.string.time_am),
        pmMarker = stringResource(Res.string.time_pm),
    )

    return when {
        diffMin < 0 -> time
        diffMin < 60 -> stringResource(Res.string.relative_in_minutes, diffMin.toInt())
        departure.date == nowLocal.date -> stringResource(Res.string.relative_today, time)
        departure.date == nowLocal.date.plus(1, DateTimeUnit.DAY) ->
            stringResource(Res.string.relative_tomorrow, time)

        else -> {
            val dayNames = stringArrayResource(Res.array.day_names_short)
            stringResource(
                Res.string.relative_day_at_time,
                dayNames[departure.dayOfWeek.ordinal],
                time,
            )
        }
    }
}

fun formatLongDate(
    year: Int,
    month: Int,
    day: Int,
    dayNames: List<String>,
    monthNames: List<String>,
    connector: String
): String {
    val date = LocalDate(year, month, day)
    val dayName = dayNames[date.dayOfWeek.ordinal]
    val monthName = monthNames[date.month.ordinal]
    return "$dayName $day $connector $monthName"
}

/** Today/Tomorrow/This-week/Later bucket for a list sorted by relative departure date. */
enum class RelativeDateGroup { TODAY, TOMORROW, THIS_WEEK, LATER }

/**
 * Buckets [items] into [RelativeDateGroup]s by the date [epochMsOf] resolves to, relative to
 * [nowMs]. Used by both the driver's trip list and the passenger's booking list, which must
 * agree on the same Today/Tomorrow/This-week boundary logic.
 */
fun <T> groupByRelativeDate(
    items: List<T>,
    nowMs: Long,
    epochMsOf: (T) -> Long
): List<Pair<RelativeDateGroup, List<T>>> {
    val tz = TimeZone.currentSystemDefault()
    val nowDate = Instant.fromEpochMilliseconds(nowMs).toLocalDateTime(tz).date
    val tomorrow = nowDate.plus(1, DateTimeUnit.DAY)
    val nextWeek = nowDate.plus(7, DateTimeUnit.DAY)

    val groups = LinkedHashMap<RelativeDateGroup, MutableList<T>>()
    items.forEach { item ->
        val date = Instant.fromEpochMilliseconds(epochMsOf(item)).toLocalDateTime(tz).date
        val group = when {
            date == nowDate -> RelativeDateGroup.TODAY
            date == tomorrow -> RelativeDateGroup.TOMORROW
            date < nextWeek -> RelativeDateGroup.THIS_WEEK
            else -> RelativeDateGroup.LATER
        }
        groups.getOrPut(group) { mutableListOf() }.add(item)
    }
    return groups.entries.map { (k, v) -> k to v }
}

@Composable
fun RelativeDateGroup.label(): String = when (this) {
    RelativeDateGroup.TODAY -> stringResource(Res.string.relative_date_today)
    RelativeDateGroup.TOMORROW -> stringResource(Res.string.relative_date_tomorrow)
    RelativeDateGroup.THIS_WEEK -> stringResource(Res.string.relative_date_this_week)
    RelativeDateGroup.LATER -> stringResource(Res.string.relative_date_later)
}
