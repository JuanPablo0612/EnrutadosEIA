package com.juanpablo0612.carpool.presentation.utils

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifierGregorian
import platform.Foundation.NSDateComponents
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

actual fun formatShortTime(hour: Int, minute: Int): String {
    val components = NSDateComponents().apply {
        this.hour = hour.toLong()
        this.minute = minute.toLong()
    }
    val calendar = NSCalendar(NSCalendarIdentifierGregorian)
    val date = calendar.dateFromComponents(components) ?: return "$hour:$minute"
    val formatter = NSDateFormatter().apply {
        dateFormat = "h:mm a"
        locale = NSLocale.currentLocale
    }
    return formatter.stringFromDate(date)
}

actual fun formatLongDate(year: Int, month: Int, day: Int): String {
    val components = NSDateComponents().apply {
        this.year = year.toLong()
        this.month = month.toLong()
        this.day = day.toLong()
    }
    val calendar = NSCalendar(NSCalendarIdentifierGregorian)
    val date = calendar.dateFromComponents(components) ?: return "$day/$month/$year"
    val formatter = NSDateFormatter().apply {
        dateFormat = "EEE d 'de' MMMM"
        locale = NSLocale(localeIdentifier = "es")
    }
    return formatter.stringFromDate(date)
}
