package com.juanpablo0612.carpool.presentation.utils

import kotlinx.datetime.LocalDate

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
