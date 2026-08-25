package com.juanpablo0612.carpool.presentation.route

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.day_abbr_fri
import enrutadoseia.composeapp.generated.resources.day_abbr_mon
import enrutadoseia.composeapp.generated.resources.day_abbr_sat
import enrutadoseia.composeapp.generated.resources.day_abbr_sun
import enrutadoseia.composeapp.generated.resources.day_abbr_thu
import enrutadoseia.composeapp.generated.resources.day_abbr_tue
import enrutadoseia.composeapp.generated.resources.day_abbr_wed
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.StringResource

/**
 * Days of the week paired with their abbreviation string resource, in calendar order. Shared by
 * [com.juanpablo0612.carpool.presentation.route.create.components.DaySelector] and
 * [com.juanpablo0612.carpool.presentation.route.list.components.RouteCard] so both recurrence
 * pickers render days in the same order.
 */
val orderedDays: List<Pair<DayOfWeek, StringResource>> = listOf(
    DayOfWeek.MONDAY to Res.string.day_abbr_mon,
    DayOfWeek.TUESDAY to Res.string.day_abbr_tue,
    DayOfWeek.WEDNESDAY to Res.string.day_abbr_wed,
    DayOfWeek.THURSDAY to Res.string.day_abbr_thu,
    DayOfWeek.FRIDAY to Res.string.day_abbr_fri,
    DayOfWeek.SATURDAY to Res.string.day_abbr_sat,
    DayOfWeek.SUNDAY to Res.string.day_abbr_sun
)
