package com.juanpablo0612.carpool.data.vehicle.datasource

interface VehicleStorageDataSource {
    suspend fun uploadVehiclePhoto(driverId: String, vehicleId: String, bytes: ByteArray): String
    suspend fun deleteVehiclePhoto(driverId: String, vehicleId: String)
}
