package com.juanpablo0612.carpool.domain.vehicle.usecase

import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository

class UpdateVehicleUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(vehicle: Vehicle, photoBytes: ByteArray?): Result<Unit> =
        repository.updateVehicle(vehicle, photoBytes)
}
