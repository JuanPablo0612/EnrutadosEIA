package com.juanpablo0612.carpool.domain.vehicles.use_case

import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle
import com.juanpablo0612.carpool.domain.vehicles.repository.VehicleRepository

class GetVehicleByIdUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(vehicleId: String): Result<Vehicle> =
        repository.getVehicleById(vehicleId)
}
