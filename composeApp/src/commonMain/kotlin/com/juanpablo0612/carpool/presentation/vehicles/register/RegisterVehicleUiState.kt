package com.juanpablo0612.carpool.presentation.vehicles.register

import io.github.vinceglb.filekit.PlatformFile

data class RegisterVehicleUiState(
    val brand: String = "",
    val model: String = "",
    val licensePlate: String = "",
    val color: String = "",
    val year: String = "",
    val seatsAvailable: String = "",
    val vehiclePhoto: PlatformFile? = null,
    val isLoading: Boolean = false,
    val error: RegisterVehicleError? = null
)
