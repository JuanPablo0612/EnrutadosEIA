package com.juanpablo0612.carpool.domain.vehicle.usecase

import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository

class CreateVehicleUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(vehicle: Vehicle, photoBytes: ByteArray?): Result<Unit> {
        return repository.createVehicle(vehicle, photoBytes)
    }
}
