package com.juanpablo0612.carpool.presentation.home

import androidx.compose.runtime.Composable
import com.juanpablo0612.carpool.presentation.utils.formatShortTime
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.day_names_short
import enrutadoseia.composeapp.generated.resources.home_greeting_afternoon
import enrutadoseia.composeapp.generated.resources.home_greeting_evening
import enrutadoseia.composeapp.generated.resources.home_greeting_morning
import enrutadoseia.composeapp.generated.resources.relative_day_at_time
import enrutadoseia.composeapp.generated.resources.relative_in_minutes
import enrutadoseia.composeapp.generated.resources.relative_today
import enrutadoseia.composeapp.generated.resources.relative_tomorrow
import enrutadoseia.composeapp.generated.resources.time_am
import enrutadoseia.composeapp.generated.resources.time_pm
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

/**
 * The greeting for a given local hour, as a resource for the caller to resolve with the user's
 * name. Returns the resource rather than a formatted string so the copy stays in `strings.xml`
 * and follows the device locale — this greeting is the first thing every user sees, and it used
 * to be hardcoded Spanish that rendered untranslated in the English locale.
 */
fun greetingResourceForTime(hour: Int): StringResource = when (hour) {
    in 5..11 -> Res.string.home_greeting_morning
    in 12..18 -> Res.string.home_greeting_afternoon
    else -> Res.string.home_greeting_evening
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

fun startOfCurrentMonth(now: Long): Long {
    val tz = TimeZone.currentSystemDefault()
    val local = Instant.fromEpochMilliseconds(now).toLocalDateTime(tz)
    val firstDay = LocalDateTime(local.year, local.month, 1, 0, 0, 0)
    return firstDay.toInstant(tz).toEpochMilliseconds()
}
