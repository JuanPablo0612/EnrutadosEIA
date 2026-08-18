package com.juanpablo0612.carpool.domain.vehicle.usecase

import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.domain.vehicle.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow

class GetUserVehiclesUseCase(private val repository: VehicleRepository) {
    operator fun invoke(userId: String): Flow<List<Vehicle>> {
        return repository.getUserVehicles(userId)
    }
}
