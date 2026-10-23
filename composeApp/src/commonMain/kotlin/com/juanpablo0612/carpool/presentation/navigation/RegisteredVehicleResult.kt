package com.juanpablo0612.carpool.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

private const val REGISTERED_VEHICLE_ID_KEY = "registered_vehicle_id"

/**
 * Hands the id of a vehicle just created in [Route.RegisterVehicle] to the screen that opened it,
 * then pops. Openers that don't observe it simply ignore it, so the screen works from anywhere.
 */
fun NavController.popWithRegisteredVehicle(vehicleId: String) {
    previousBackStackEntry?.savedStateHandle?.set(REGISTERED_VEHICLE_ID_KEY, vehicleId)
    popBackStack()
}

/** Calls [onResult] once for each vehicle id delivered to this entry, then clears it. */
@Composable
fun NavBackStackEntry.ObserveRegisteredVehicle(onResult: (vehicleId: String) -> Unit) {
    val currentOnResult by rememberUpdatedState(onResult)
    val vehicleId by savedStateHandle
        .getStateFlow<String?>(REGISTERED_VEHICLE_ID_KEY, null)
        .collectAsState()

    LaunchedEffect(vehicleId) {
        vehicleId?.let {
            currentOnResult(it)
            savedStateHandle.remove<String>(REGISTERED_VEHICLE_ID_KEY)
        }
    }
}
