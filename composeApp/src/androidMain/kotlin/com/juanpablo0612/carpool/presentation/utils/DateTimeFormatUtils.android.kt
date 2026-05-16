package com.juanpablo0612.carpool.presentation.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

actual fun formatShortTime(hour: Int, minute: Int): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
    }
    return SimpleDateFormat("h:mm a", Locale.getDefault()).format(calendar.time)
}

actual fun formatLongDate(year: Int, month: Int, day: Int): String {
    val calendar = Calendar.getInstance().apply {
        set(year, month - 1, day)
    }
    return SimpleDateFormat("EEE d 'de' MMMM", Locale.forLanguageTag("es")).format(calendar.time)
}
