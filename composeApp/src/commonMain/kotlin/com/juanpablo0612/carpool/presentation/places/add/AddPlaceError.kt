package com.juanpablo0612.carpool.presentation.places.add

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_place_address_required
import enrutadoseia.composeapp.generated.resources.error_place_name_required
import enrutadoseia.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.StringResource

sealed class AddPlaceError {
    data object NameRequired : AddPlaceError()
    data object AddressRequired : AddPlaceError()
    data object Unknown : AddPlaceError()

    fun asStringResource(): StringResource = when (this) {
        NameRequired -> Res.string.error_place_name_required
        AddressRequired -> Res.string.error_place_address_required
        Unknown -> Res.string.error_unknown
    }
}
