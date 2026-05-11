package com.juanpablo0612.carpool.presentation.home

import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun greetingForTime(hour: Int, firstName: String): String = when (hour) {
    in 0..4 -> "Buenas noches, $firstName"
    in 5..11 -> "Buenos días, $firstName"
    in 12..18 -> "Buenas tardes, $firstName"
    else -> "Buenas noches, $firstName"
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
