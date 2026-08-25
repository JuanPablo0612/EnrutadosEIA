package com.juanpablo0612.carpool.domain.place.service

import com.juanpablo0612.carpool.domain.place.model.Coordinates

interface LocationService {
    suspend fun getCurrentCoordinates(): Coordinates?
}
