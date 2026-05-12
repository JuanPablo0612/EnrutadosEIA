package com.juanpablo0612.carpool.presentation.utils

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

actual fun formatShortTime(hour: Int, minute: Int): String {
    val time = LocalTime.of(hour, minute)
    val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        .withLocale(Locale.getDefault())
    return time.format(formatter)
}

actual fun formatLongDate(year: Int, month: Int, day: Int): String {
    val date = LocalDate.of(year, month, day)
    val formatter = DateTimeFormatter.ofPattern("EEE d 'de' MMMM", Locale("es"))
    return date.format(formatter)
}
