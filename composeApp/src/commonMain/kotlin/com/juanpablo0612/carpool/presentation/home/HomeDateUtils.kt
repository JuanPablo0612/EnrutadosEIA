package com.juanpablo0612.carpool.presentation.home

import androidx.compose.runtime.Composable
import com.juanpablo0612.carpool.presentation.ui.util.formatLongDate
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.date_of_connector
import enrutadoseia.composeapp.generated.resources.day_names_long
import enrutadoseia.composeapp.generated.resources.home_greeting_afternoon
import enrutadoseia.composeapp.generated.resources.home_greeting_evening
import enrutadoseia.composeapp.generated.resources.home_greeting_morning
import enrutadoseia.composeapp.generated.resources.month_names
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

/**
 * The greeting for a given local hour, as a resource for the caller to resolve with the user's
 * name. Returns the resource rather than a formatted string so the copy stays in `strings.xml`
 * and follows the device locale.
 */
fun greetingResourceForTime(hour: Int): StringResource = when (hour) {
    in 5..11 -> Res.string.home_greeting_morning
    in 12..18 -> Res.string.home_greeting_afternoon
    else -> Res.string.home_greeting_evening
}

/** Today as "Jueves 1 de octubre": the overline above the greeting. */
@Composable
fun homeHeaderDate(date: LocalDate): String {
    val text = formatLongDate(
        year = date.year,
        month = date.month.ordinal + 1,
        day = date.day,
        dayNames = stringArrayResource(Res.array.day_names_long),
        monthNames = stringArrayResource(Res.array.month_names),
        connector = stringResource(Res.string.date_of_connector),
    )
    // Weekday names are lowercase in Spanish mid-sentence; this one starts the line.
    return text.replaceFirstChar { it.uppercaseChar() }
}
