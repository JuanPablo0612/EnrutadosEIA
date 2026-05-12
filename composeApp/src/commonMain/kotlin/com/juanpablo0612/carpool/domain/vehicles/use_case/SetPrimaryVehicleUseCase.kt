package com.juanpablo0612.carpool.domain.vehicles.use_case

import com.juanpablo0612.carpool.domain.vehicles.repository.VehicleRepository

class SetPrimaryVehicleUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(userId: String, vehicleId: String): Result<Unit> =
        repository.setPrimaryVehicle(userId, vehicleId)
}
