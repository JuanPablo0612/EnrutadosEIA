package com.juanpablo0612.carpool.domain.places.service

import com.juanpablo0612.carpool.domain.places.model.Coordinates

interface LocationService {
    suspend fun getCurrentCoordinates(): Coordinates?
}
