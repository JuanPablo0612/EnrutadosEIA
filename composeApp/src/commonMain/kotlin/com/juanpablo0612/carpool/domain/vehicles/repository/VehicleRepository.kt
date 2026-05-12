package com.juanpablo0612.carpool.domain.vehicles.repository

import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    suspend fun createVehicle(vehicle: Vehicle, photoBytes: ByteArray): Result<Unit>
    suspend fun getVehicleById(vehicleId: String): Result<Vehicle>
    suspend fun updateVehicle(vehicle: Vehicle, photoBytes: ByteArray?): Result<Unit>
    suspend fun deleteVehicle(vehicleId: String, driverId: String): Result<Unit>
    suspend fun setPrimaryVehicle(userId: String, vehicleId: String): Result<Unit>
    fun getUserVehicles(userId: String): Flow<List<Vehicle>>
    fun getDriverVehicles(driverId: String): Flow<List<Vehicle>>
}
