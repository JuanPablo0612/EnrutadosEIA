package com.juanpablo0612.carpool.presentation.home

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.home_greeting_afternoon
import enrutadoseia.composeapp.generated.resources.home_greeting_evening
import enrutadoseia.composeapp.generated.resources.home_greeting_morning
import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.StringResource

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

fun startOfCurrentMonth(now: Long): Long {
    val tz = TimeZone.currentSystemDefault()
    val local = Instant.fromEpochMilliseconds(now).toLocalDateTime(tz)
    val firstDay = LocalDateTime(local.year, local.month, 1, 0, 0, 0)
    return firstDay.toInstant(tz).toEpochMilliseconds()
}
