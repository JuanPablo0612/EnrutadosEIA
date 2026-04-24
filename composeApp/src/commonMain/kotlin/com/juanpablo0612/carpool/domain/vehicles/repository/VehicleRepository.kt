package com.juanpablo0612.carpool.domain.vehicles.repository

import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    suspend fun createVehicle(vehicle: Vehicle, photoBytes: ByteArray): Result<Unit>
    fun getUserVehicles(userId: String): Flow<List<Vehicle>>
    fun getDriverVehicles(driverId: String): Flow<List<Vehicle>>
}
