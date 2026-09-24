package com.juanpablo0612.carpool.presentation.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.day_names_short
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

/**
 * A departure time phrased relative to [now] — "In 20 min", "Today · 7:05 AM", "Tue. · 7:05 AM".
 *
 * Composable because every phrasing, the AM/PM markers and the weekday abbreviations all come from
 * `strings.xml`; this used to build Spanish literals in Kotlin, so it rendered untranslated in the
 * English locale.
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
