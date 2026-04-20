package com.juanpablo0612.carpool.domain.vehicles.use_case

import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle
import com.juanpablo0612.carpool.domain.vehicles.repository.VehicleRepository

class CreateVehicleUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(vehicle: Vehicle, photoBytes: ByteArray): Result<Unit> {
        return repository.createVehicle(vehicle, photoBytes)
    }
}
