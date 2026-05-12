package com.juanpablo0612.carpool.presentation.vehicles.register

import com.juanpablo0612.carpool.domain.vehicles.model.VehicleType
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.datetime.LocalDate

sealed class RegisterVehicleAction {
    data class OnPhotoSelected(val photo: PlatformFile) : RegisterVehicleAction()
    data class OnBrandSelected(val brand: String) : RegisterVehicleAction()
    data object OnToggleBrandDropdown : RegisterVehicleAction()
    data object OnToggleCustomBrand : RegisterVehicleAction()
    data class OnModelChanged(val model: String) : RegisterVehicleAction()
    data class OnPlateChanged(val plate: String) : RegisterVehicleAction()
    data class OnColorSelected(val color: String) : RegisterVehicleAction()
    data class OnCustomColorChanged(val color: String) : RegisterVehicleAction()
    data class OnYearSelected(val year: Int) : RegisterVehicleAction()
    data object OnToggleYearDropdown : RegisterVehicleAction()
    data class OnSeatCountChanged(val count: Int) : RegisterVehicleAction()
    data class OnTypeSelected(val type: VehicleType?) : RegisterVehicleAction()
    data class OnSoatDateSelected(val date: LocalDate) : RegisterVehicleAction()
    data class OnTecnoDateSelected(val date: LocalDate) : RegisterVehicleAction()
    data object OnToggleDocuments : RegisterVehicleAction()
    data object OnShowPhotoSheet : RegisterVehicleAction()
    data object OnDismissPhotoSheet : RegisterVehicleAction()
    data object OnShowSoatDatePicker : RegisterVehicleAction()
    data object OnDismissSoatDatePicker : RegisterVehicleAction()
    data object OnShowTecnoDatePicker : RegisterVehicleAction()
    data object OnDismissTecnoDatePicker : RegisterVehicleAction()
    data object OnSaveClick : RegisterVehicleAction()
    data object OnBackClick : RegisterVehicleAction()
}
