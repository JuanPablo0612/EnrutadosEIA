package com.juanpablo0612.carpool.data.place.datasource

import com.juanpablo0612.carpool.core.location.iosLocationPermissionController
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.mobile

// The shared controller instead of compass's own. The default dispatcher stays: the availability
// check compass runs on it is a blocking call to the location daemon, which must stay off main.
internal actual fun createGeolocator(): Geolocator =
    Geolocator.mobile(permissionController = iosLocationPermissionController)
