package com.juanpablo0612.carpool.presentation.home

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.home_greeting_afternoon
import enrutadoseia.composeapp.generated.resources.home_greeting_evening
import enrutadoseia.composeapp.generated.resources.home_greeting_morning
import kotlin.time.Clock
import kotlin.time.Instant
import org.jetbrains.compose.resources.StringResource
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

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

fun relativeTime(epochMs: Long, now: Long = Clock.System.now().toEpochMilliseconds()): String {
    val tz = TimeZone.currentSystemDefault()
    val departure = Instant.fromEpochMilliseconds(epochMs).toLocalDateTime(tz)
    val nowLocal = Instant.fromEpochMilliseconds(now).toLocalDateTime(tz)
    val diffMin = (epochMs - now) / 60_000

    return when {
        diffMin < 0 -> formatTime(departure)
        diffMin < 60 -> "En $diffMin min"
        departure.date == nowLocal.date -> "Hoy · ${formatTime(departure)}"
        departure.date == nowLocal.date.plus(1, DateTimeUnit.DAY) -> "Mañana · ${formatTime(departure)}"
        else -> "${shortDayName(departure.dayOfWeek)} · ${formatTime(departure)}"
    }
}

fun startOfCurrentMonth(now: Long): Long {
    val tz = TimeZone.currentSystemDefault()
    val local = Instant.fromEpochMilliseconds(now).toLocalDateTime(tz)
    val firstDay = LocalDateTime(local.year, local.month, 1, 0, 0, 0)
    return firstDay.toInstant(tz).toEpochMilliseconds()
}

private fun formatTime(ldt: LocalDateTime): String {
    val h12 = ldt.hour % 12
    val displayHour = if (h12 == 0) 12 else h12
    val amPm = if (ldt.hour < 12) "a. m." else "p. m."
    val min = ldt.minute.toString().padStart(2, '0')
    return "$displayHour:$min $amPm"
}

private fun shortDayName(dow: DayOfWeek): String = when (dow) {
    DayOfWeek.MONDAY -> "Lun"
    DayOfWeek.TUESDAY -> "Mar"
    DayOfWeek.WEDNESDAY -> "Mié"
    DayOfWeek.THURSDAY -> "Jue"
    DayOfWeek.FRIDAY -> "Vie"
    DayOfWeek.SATURDAY -> "Sáb"
    DayOfWeek.SUNDAY -> "Dom"
}
