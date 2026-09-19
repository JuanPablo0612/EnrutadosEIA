package com.juanpablo0612.carpool.presentation.vehicle.list

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_vehicle_delete_failed
import enrutadoseia.composeapp.generated.resources.error_vehicle_set_primary_failed
import org.jetbrains.compose.resources.StringResource

sealed class VehiclesListError {
    data object DeleteFailed : VehiclesListError()
    data object SetPrimaryFailed : VehiclesListError()

    fun asStringResource(): StringResource = when (this) {
        DeleteFailed -> Res.string.error_vehicle_delete_failed
        SetPrimaryFailed -> Res.string.error_vehicle_set_primary_failed
    }
}
