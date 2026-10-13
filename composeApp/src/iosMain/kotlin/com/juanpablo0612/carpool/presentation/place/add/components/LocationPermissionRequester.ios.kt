package com.juanpablo0612.carpool.presentation.place.add.components

import dev.jordond.compass.Priority
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.compass.permissions.PermissionState
import dev.jordond.compass.permissions.mobile

// dev.jordond.compass:permissions-mobile ships a CLLocationManager-backed implementation, the same
// controller CompassLocationService's Geolocator.mobile() relies on for current() on this platform.
// Requires NSLocationWhenInUseUsageDescription in Info.plist.
private class IosLocationPermissionRequester(
    private val controller: LocationPermissionController = LocationPermissionController.mobile(),
) : LocationPermissionRequester {

    override fun hasPermission(): Boolean = controller.hasPermission()

    override suspend fun requestPermission(): Boolean =
        controller.requirePermissionFor(Priority.HighAccuracy) == PermissionState.Granted
}

actual fun createLocationPermissionRequester(): LocationPermissionRequester =
    IosLocationPermissionRequester()
