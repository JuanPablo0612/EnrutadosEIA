package com.juanpablo0612.carpool.presentation.place.selector

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_suggestion_unavailable
import enrutadoseia.composeapp.generated.resources.place_selector_location_error
import org.jetbrains.compose.resources.StringResource

/**
 * Screen-local error for [PlaceSelectorViewModel] — never touches the domain layer, so it
 * follows the self-contained presentation error style (see AddPlaceError) rather than a
 * mapped-from-repository sealed class + mapper.
 */
sealed class PlaceSelectorError {
    data object LocationUnavailable : PlaceSelectorError()
    data object SuggestionUnavailable : PlaceSelectorError()

    fun asStringResource(): StringResource = when (this) {
        LocationUnavailable -> Res.string.place_selector_location_error
        SuggestionUnavailable -> Res.string.error_suggestion_unavailable
    }
}
