package com.juanpablo0612.carpool.data.place.datasource

import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.mobile

internal actual fun createGeolocator(): Geolocator = Geolocator.mobile()
