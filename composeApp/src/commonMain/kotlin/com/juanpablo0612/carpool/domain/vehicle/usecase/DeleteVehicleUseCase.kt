package com.juanpablo0612.carpool.domain.vehicle.usecase

import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository

class DeleteVehicleUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(vehicleId: String, driverId: String): Result<Unit> =
        repository.deleteVehicle(vehicleId, driverId)
}
