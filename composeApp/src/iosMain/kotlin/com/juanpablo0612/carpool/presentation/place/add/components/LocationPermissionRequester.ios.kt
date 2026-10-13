package com.juanpablo0612.carpool.presentation.place.add.components

import com.juanpablo0612.carpool.core.location.iosLocationPermissionController
import dev.jordond.compass.Priority
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.compass.permissions.PermissionState

// Asks for When-In-Use access, so Info.plist needs only NSLocationWhenInUseUsageDescription.
private class IosLocationPermissionRequester(
    private val controller: LocationPermissionController = iosLocationPermissionController,
) : LocationPermissionRequester {

    override fun hasPermission(): Boolean = controller.hasPermission()

    override suspend fun requestPermission(): Boolean =
        controller.requirePermissionFor(Priority.HighAccuracy) == PermissionState.Granted
}

actual fun createLocationPermissionRequester(): LocationPermissionRequester =
    IosLocationPermissionRequester()
