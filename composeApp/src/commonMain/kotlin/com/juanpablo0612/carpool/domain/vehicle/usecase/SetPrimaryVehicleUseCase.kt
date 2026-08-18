package com.juanpablo0612.carpool.domain.vehicle.usecase

import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository

class SetPrimaryVehicleUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(userId: String, vehicleId: String): Result<Unit> =
        repository.setPrimaryVehicle(userId, vehicleId)
}
