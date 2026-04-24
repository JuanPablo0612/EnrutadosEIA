package com.juanpablo0612.carpool.domain.vehicles.use_case

import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle
import com.juanpablo0612.carpool.domain.vehicles.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow

class GetDriverVehiclesUseCase(private val repository: VehicleRepository) {
    operator fun invoke(driverId: String): Flow<List<Vehicle>> =
        repository.getDriverVehicles(driverId)
}
