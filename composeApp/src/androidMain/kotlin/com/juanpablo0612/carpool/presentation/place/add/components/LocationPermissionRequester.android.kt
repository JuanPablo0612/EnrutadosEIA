package com.juanpablo0612.carpool.presentation.place.add.components

import dev.jordond.compass.Priority
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.compass.permissions.PermissionState
import dev.jordond.compass.permissions.mobile

// dev.jordond.compass:permissions-mobile auto-initializes its ContextProvider/ActivityProvider via
// an androidx.startup Initializer merged into the manifest by this dependency — no manual setup
// needed in CarpoolApplication/MainActivity. requirePermissionFor() drives the real
// ActivityResultContracts.RequestMultiplePermissions() system dialog.
private class AndroidLocationPermissionRequester(
    private val controller: LocationPermissionController = LocationPermissionController.mobile(),
) : LocationPermissionRequester {

    override fun hasPermission(): Boolean = controller.hasPermission()

    override suspend fun requestPermission(): Boolean =
        controller.requirePermissionFor(Priority.HighAccuracy) == PermissionState.Granted
}

actual fun createLocationPermissionRequester(): LocationPermissionRequester =
    AndroidLocationPermissionRequester()
