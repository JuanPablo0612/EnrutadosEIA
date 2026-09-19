package com.juanpablo0612.carpool.presentation.place.picker

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.map_picker_error_location_permission_denied
import enrutadoseia.composeapp.generated.resources.map_picker_error_location_unavailable
import org.jetbrains.compose.resources.StringResource

sealed class MapPickerError {
    data object LocationPermissionDenied : MapPickerError()
    data object LocationUnavailable : MapPickerError()

    fun asStringResource(): StringResource = when (this) {
        LocationPermissionDenied -> Res.string.map_picker_error_location_permission_denied
        LocationUnavailable -> Res.string.map_picker_error_location_unavailable
    }
}
