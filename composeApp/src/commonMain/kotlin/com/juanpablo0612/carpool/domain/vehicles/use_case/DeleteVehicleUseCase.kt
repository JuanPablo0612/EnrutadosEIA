package com.juanpablo0612.carpool.domain.vehicles.use_case

import com.juanpablo0612.carpool.domain.vehicles.repository.VehicleRepository

class DeleteVehicleUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(vehicleId: String, driverId: String): Result<Unit> =
        repository.deleteVehicle(vehicleId, driverId)
}
