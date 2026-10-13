package com.juanpablo0612.carpool.data.place.datasource

import com.juanpablo0612.carpool.core.location.iosLocationPermissionController
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.mobile
import kotlinx.coroutines.Dispatchers

// The shared controller instead of compass's own, and the main dispatcher so the locator's
// CLLocationManager is driven from the thread that receives its callbacks.
internal actual fun createGeolocator(): Geolocator = Geolocator.mobile(
    permissionController = iosLocationPermissionController,
    dispatcher = Dispatchers.Main,
)
