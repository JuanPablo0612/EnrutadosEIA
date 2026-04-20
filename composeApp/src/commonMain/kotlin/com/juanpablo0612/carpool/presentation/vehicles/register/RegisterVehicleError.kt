package com.juanpablo0612.carpool.presentation.vehicles.register

import enrutadoseia.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource

sealed class RegisterVehicleError {
    data object BrandRequired : RegisterVehicleError()
    data object ModelRequired : RegisterVehicleError()
    data object LicensePlateRequired : RegisterVehicleError()
    data object ColorRequired : RegisterVehicleError()
    data object YearRequired : RegisterVehicleError()
    data object YearInvalid : RegisterVehicleError()
    data object SeatsRequired : RegisterVehicleError()
    data object SeatsInvalid : RegisterVehicleError()
    data object PhotoRequired : RegisterVehicleError()
    data object UserNotAuthenticated : RegisterVehicleError()
    data object Unknown : RegisterVehicleError()

    fun asStringResource(): StringResource = when (this) {
        BrandRequired -> Res.string.error_vehicle_brand_required
        ModelRequired -> Res.string.error_vehicle_model_required
        LicensePlateRequired -> Res.string.error_vehicle_license_plate_required
        ColorRequired -> Res.string.error_vehicle_color_required
        YearRequired -> Res.string.error_vehicle_year_required
        YearInvalid -> Res.string.error_vehicle_year_invalid
        SeatsRequired -> Res.string.error_vehicle_seats_required
        SeatsInvalid -> Res.string.error_vehicle_seats_invalid
        PhotoRequired -> Res.string.error_vehicle_photo_required
        UserNotAuthenticated -> Res.string.error_user_not_authenticated
        Unknown -> Res.string.error_unknown
    }
}
