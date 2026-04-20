package com.juanpablo0612.carpool.presentation.vehicles.register

import io.github.vinceglb.filekit.PlatformFile


sealed class RegisterVehicleAction {
    data class OnBrandChanged(val brand: String) : RegisterVehicleAction()
    data class OnModelChanged(val model: String) : RegisterVehicleAction()
    data class OnLicensePlateChanged(val licensePlate: String) : RegisterVehicleAction()
    data class OnColorChanged(val color: String) : RegisterVehicleAction()
    data class OnYearChanged(val year: String) : RegisterVehicleAction()
    data class OnSeatsChanged(val seats: String) : RegisterVehicleAction()
    data class OnPhotoSelected(val photo: PlatformFile) : RegisterVehicleAction()
    data object OnSaveClick : RegisterVehicleAction()
    data object OnBackClick : RegisterVehicleAction()
}
