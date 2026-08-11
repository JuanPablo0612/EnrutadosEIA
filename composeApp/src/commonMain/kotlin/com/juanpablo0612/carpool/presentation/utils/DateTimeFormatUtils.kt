package com.juanpablo0612.carpool.presentation.utils

import kotlinx.datetime.LocalDate
import kotlin.math.round

fun formatShortTime(hour: Int, minute: Int, amMarker: String, pmMarker: String): String {
    val h12 = if (hour % 12 == 0) 12 else hour % 12
    val amPm = if (hour < 12) amMarker else pmMarker
    val min = minute.toString().padStart(2, '0')
    return "$h12:$min $amPm"
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

/**
 * Formats a latitude/longitude pair to a fixed number of decimal places without relying on
 * `String.format`, which is JVM-only and unavailable in common code.
 */
fun formatCoordinates(latitude: Double, longitude: Double, decimals: Int = 5): String {
    return "${roundToDecimals(latitude, decimals)}, ${roundToDecimals(longitude, decimals)}"
}

private fun roundToDecimals(value: Double, decimals: Int): String {
    val factor = generateSequence(1L) { it * 10 }.elementAt(decimals)
    val scaled = round(value * factor).toLong()
    val negative = scaled < 0
    val absScaled = if (negative) -scaled else scaled
    val wholePart = absScaled / factor
    val fractionPart = (absScaled % factor).toString().padStart(decimals, '0')
    return "${if (negative) "-" else ""}$wholePart.$fractionPart"
}
