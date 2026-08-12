package com.juanpablo0612.carpool.presentation.places.add.components

/**
 * Checks and requests the device's runtime location permission (ACCESS_FINE_LOCATION /
 * ACCESS_COARSE_LOCATION on Android). Declaring the permission in the manifest is not enough —
 * without an explicit request, GoogleMap's `isMyLocationEnabled` throws a SecurityException and
 * CompassLocationService silently returns null (3.12).
 *
 * Backed on both platforms by the same dev.jordond.compass.permissions controller that
 * CompassLocationService's Geolocator.mobile() already consults internally when fetching a
 * location, so [hasPermission] / [requestPermission] never disagree with what a location fetch
 * will actually do.
 */
interface LocationPermissionRequester {

    /** True if the OS has already granted fine or coarse location access. */
    fun hasPermission(): Boolean

    /**
     * Requests the permission if it isn't already granted (suspending until the user responds to
     * the system dialog), returning the resulting grant state. Safe to call repeatedly — it's a
     * no-op once permission is already granted.
     */
    suspend fun requestPermission(): Boolean
}

/** Creates the platform [LocationPermissionRequester], matching the CompassFactory pattern. */
expect fun createLocationPermissionRequester(): LocationPermissionRequester
