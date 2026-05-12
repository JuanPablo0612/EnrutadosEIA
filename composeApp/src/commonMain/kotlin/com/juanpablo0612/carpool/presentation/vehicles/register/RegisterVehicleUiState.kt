package com.juanpablo0612.carpool.presentation.vehicles.register

import com.juanpablo0612.carpool.domain.vehicles.model.VehicleType
import io.github.vinceglb.filekit.PlatformFile
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class RegisterVehicleUiState(
    val mode: Mode = Mode.Create,
    val vehicleId: String = "",
    val existingPhotoUrl: String? = null,
    val photoFile: PlatformFile? = null,
    val brand: String = "",
    val isCustomBrand: Boolean = false,
    val model: String = "",
    val plate: String = "",
    val color: String = "",
    val isCustomColor: Boolean = false,
    val customColor: String = "",
    val year: Int = currentYear(),
    val seatCount: Int = 3,
    val type: VehicleType? = null,
    val soatDate: LocalDate? = null,
    val tecnomecanicaDate: LocalDate? = null,
    val showDocuments: Boolean = false,
    val showBrandDropdown: Boolean = false,
    val showYearDropdown: Boolean = false,
    val showPhotoSheet: Boolean = false,
    val showSoatDatePicker: Boolean = false,
    val showTecnoDatePicker: Boolean = false,
    val isSaving: Boolean = false,
    val brandError: Boolean = false,
    val modelError: Boolean = false,
    val plateError: Boolean = false,
    val colorError: Boolean = false,
    val generalError: RegisterVehicleError? = null,
) {
    enum class Mode { Create, Edit }

    val effectiveColor: String get() = if (isCustomColor) customColor else color

    val isValid: Boolean
        get() = brand.isNotBlank()
            && model.isNotBlank()
            && PLATE_REGEX.matches(plate)
            && effectiveColor.isNotBlank()
            && seatCount in 1..7

    companion object {
        val PLATE_REGEX = Regex("^[A-Z]{3}[0-9]{3}$")

        val COMMON_BRANDS = listOf(
            "Renault", "Chevrolet", "Mazda", "Toyota", "Kia",
            "Nissan", "Volkswagen", "Hyundai", "Ford", "Honda",
            "Mitsubishi", "Suzuki"
        )

        val PRESET_COLORS = listOf(
            "Blanco", "Negro", "Gris", "Plateado", "Rojo", "Azul"
        )
    }
}

private fun currentYear(): Int =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.year
