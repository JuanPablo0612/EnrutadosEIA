package com.juanpablo0612.carpool.data.place.service

import com.juanpablo0612.carpool.domain.place.model.Coordinates
import com.juanpablo0612.carpool.domain.place.service.LocationService
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.GeolocatorResult

class CompassLocationService : LocationService {
    private val geolocator by lazy { createGeolocator() }

    override suspend fun getCurrentCoordinates(): Coordinates? {
        return when (val result = geolocator.current(Priority.HighAccuracy)) {
            is GeolocatorResult.Success -> result.data.coordinates.let {
                Coordinates(it.latitude, it.longitude)
            }
            else -> null
        }
    }
}
