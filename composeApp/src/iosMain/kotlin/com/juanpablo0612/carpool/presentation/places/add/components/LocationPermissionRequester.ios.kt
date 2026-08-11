package com.juanpablo0612.carpool.presentation.places.add.components

import dev.jordond.compass.Priority
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.compass.permissions.PermissionState
import dev.jordond.compass.permissions.mobile

// Compiler-unverified here (no macOS toolchain, same caveat as StorageUpload.ios.kt from an
// earlier phase) but not a stub: dev.jordond.compass:permissions-mobile ships a real iOS
// CLLocationManager-backed implementation, and it's the exact same controller
// CompassLocationService's Geolocator.mobile() already depends on for current() on this platform.
// Requires NSLocationWhenInUseUsageDescription in Info.plist (added).
private class IosLocationPermissionRequester(
    private val controller: LocationPermissionController = LocationPermissionController.mobile(),
) : LocationPermissionRequester {

    override fun hasPermission(): Boolean = controller.hasPermission()

    override suspend fun requestPermission(): Boolean =
        controller.requirePermissionFor(Priority.HighAccuracy) == PermissionState.Granted
}

actual fun createLocationPermissionRequester(): LocationPermissionRequester =
    IosLocationPermissionRequester()
