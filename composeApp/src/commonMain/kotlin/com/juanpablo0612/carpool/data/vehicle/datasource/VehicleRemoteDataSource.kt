package com.juanpablo0612.carpool.data.vehicle.datasource

import com.juanpablo0612.carpool.data.vehicle.model.VehicleDto
import kotlinx.coroutines.flow.Flow

interface VehicleRemoteDataSource {
    fun newVehicleDocumentId(): String
    suspend fun createVehicle(vehicle: VehicleDto)
    suspend fun getVehicleById(vehicleId: String): VehicleDto
    suspend fun updateVehicle(vehicle: VehicleDto)
    suspend fun deleteVehicle(vehicleId: String)
    suspend fun setPrimaryVehicle(vehicles: List<VehicleDto>, vehicleId: String)
    fun getUserVehicles(userId: String): Flow<List<VehicleDto>>
}
