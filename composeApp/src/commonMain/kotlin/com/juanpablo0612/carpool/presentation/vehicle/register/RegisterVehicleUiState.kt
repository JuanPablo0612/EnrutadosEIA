package com.juanpablo0612.carpool.presentation.vehicle.register

import com.juanpablo0612.carpool.domain.vehicle.model.VehicleType
import io.github.vinceglb.filekit.PlatformFile

data class RegisterVehicleUiState(
    val mode: Mode = Mode.Create,
    val vehicleId: String = "",
    val existingPhotoUrl: String? = null,
    val photoFile: PlatformFile? = null,
    val brand: String = "",
    val model: String = "",
    val plate: String = "",
    val color: String = "",
    val isCustomColor: Boolean = false,
    val customColor: String = "",
    val year: Int? = null,
    val seatCount: Int = 3,
    val type: VehicleType? = null,
    val isPrimary: Boolean = false,
    val showYearDropdown: Boolean = false,
    val showPhotoSheet: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val showDiscardConfirm: Boolean = false,
    val initialSnapshot: VehicleFormSnapshot? = null,
    val brandError: Boolean = false,
    val modelError: Boolean = false,
    val plateError: Boolean = false,
    val colorError: Boolean = false,
    val yearError: Boolean = false,
    val generalError: RegisterVehicleError? = null,
) {
    enum class Mode { Create, Edit }

    val effectiveColor: String get() = if (isCustomColor) customColor else color

    val snapshot: VehicleFormSnapshot
        get() = VehicleFormSnapshot(
            brand = brand,
            model = model,
            plate = plate,
            color = color,
            isCustomColor = isCustomColor,
            customColor = customColor,
            year = year,
            seatCount = seatCount,
            type = type,
            hasNewPhoto = photoFile != null,
            existingPhotoUrl = existingPhotoUrl,
        )

    val isDirty: Boolean
        get() = initialSnapshot != null && initialSnapshot != snapshot

    companion object {
        val PLATE_REGEX = Regex("^[A-Z]{3}[0-9]{3}$")

        val PRESET_COLORS = listOf(
            "Blanco", "Negro", "Gris", "Plateado", "Rojo", "Azul"
        )
    }
}

data class VehicleFormSnapshot(
    val brand: String,
    val model: String,
    val plate: String,
    val color: String,
    val isCustomColor: Boolean,
    val customColor: String,
    val year: Int?,
    val seatCount: Int,
    val type: VehicleType?,
    val hasNewPhoto: Boolean,
    val existingPhotoUrl: String?,
)
