package com.juanpablo0612.carpool.domain.vehicle.usecase

import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository

class GetVehicleByIdUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(vehicleId: String): Result<Vehicle> =
        repository.getVehicleById(vehicleId)
}
