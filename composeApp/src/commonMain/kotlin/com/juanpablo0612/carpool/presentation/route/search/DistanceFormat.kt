package com.juanpablo0612.carpool.presentation.route.search

import androidx.compose.runtime.Composable
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.distance_km
import enrutadoseia.composeapp.generated.resources.distance_meters
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/**
 * A walking distance for display: whole meters rounded to 50 below 1 km (never below 50 m), and
 * kilometres with one decimal above. The decimal separator comes from the locale's string.
 */
@Composable
fun formatDistance(meters: Double): String {
    return if (meters < 1_000) {
        val rounded = ((meters / 50).roundToInt() * 50).coerceAtLeast(50)
        stringResource(Res.string.distance_meters, rounded)
    } else {
        val tenths = (meters / 100).roundToInt()
        stringResource(Res.string.distance_km, tenths / 10, tenths % 10)
    }
}
