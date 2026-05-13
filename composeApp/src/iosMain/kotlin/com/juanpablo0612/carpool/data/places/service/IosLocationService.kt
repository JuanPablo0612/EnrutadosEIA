package com.juanpablo0612.carpool.data.places.service

import com.juanpablo0612.carpool.domain.places.model.Coordinates
import com.juanpablo0612.carpool.domain.places.service.LocationService

class IosLocationService : LocationService {
    override suspend fun getCurrentCoordinates(): Coordinates? = null
}
