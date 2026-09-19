package com.juanpablo0612.carpool.presentation.vehicle.register

import enrutadoseia.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource

// Year and seat count have no Required/Invalid cases: year comes from a closed dropdown
// (VehicleYearSection) and seats from a stepper bounded to 1..7 (VehicleSeatsSection,
// NumberStepper), so neither field can ever reach saveVehicle() in an invalid state.
sealed class RegisterVehicleError {
    data object BrandRequired : RegisterVehicleError()
    data object ModelRequired : RegisterVehicleError()
    data object LicensePlateRequired : RegisterVehicleError()
    data object ColorRequired : RegisterVehicleError()
    data object UserNotAuthenticated : RegisterVehicleError()
    data object Unknown : RegisterVehicleError()

    fun asStringResource(): StringResource = when (this) {
        BrandRequired -> Res.string.error_vehicle_brand_required
        ModelRequired -> Res.string.error_vehicle_model_required
        LicensePlateRequired -> Res.string.error_vehicle_license_plate_required
        ColorRequired -> Res.string.error_vehicle_color_required
        UserNotAuthenticated -> Res.string.error_user_not_authenticated
        Unknown -> Res.string.error_unknown
    }
}
