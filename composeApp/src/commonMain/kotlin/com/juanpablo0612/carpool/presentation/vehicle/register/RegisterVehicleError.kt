package com.juanpablo0612.carpool.presentation.vehicle.register

import enrutadoseia.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource

// Seat count has no Required/Invalid case: it comes from a stepper bounded to 1..7
// (VehicleSeatsSection, NumberStepper), so it can never reach saveVehicle() invalid.
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
