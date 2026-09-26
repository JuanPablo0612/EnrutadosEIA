package com.juanpablo0612.carpool.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * The location [Route.MapPicker] hands back to the screen that opened it. Travels through the
 * opener's `SavedStateHandle` as JSON, so a place name containing commas round-trips intact.
 */
@Serializable
data class MapPickResult(
    val latitude: Double,
    val longitude: Double,
    val placeName: String? = null,
)

private const val MAP_PICK_RESULT_KEY = "map_pick_result"

/** Delivers [result] to the previous back-stack entry and pops the map picker. */
fun NavController.popWithMapPickResult(result: MapPickResult) {
    previousBackStackEntry?.savedStateHandle?.set(MAP_PICK_RESULT_KEY, Json.encodeToString(result))
    popBackStack()
}

/** Calls [onResult] once for each [MapPickResult] delivered to this entry, then clears it. */
@Composable
fun NavBackStackEntry.ObserveMapPickResult(onResult: (MapPickResult) -> Unit) {
    val currentOnResult by rememberUpdatedState(onResult)
    val encoded by savedStateHandle
        .getStateFlow<String?>(MAP_PICK_RESULT_KEY, null)
        .collectAsState()

    LaunchedEffect(encoded) {
        encoded?.let {
            currentOnResult(Json.decodeFromString<MapPickResult>(it))
            savedStateHandle.remove<String>(MAP_PICK_RESULT_KEY)
        }
    }
}
