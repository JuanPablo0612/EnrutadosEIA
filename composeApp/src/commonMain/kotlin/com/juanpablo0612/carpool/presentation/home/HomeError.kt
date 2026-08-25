package com.juanpablo0612.carpool.presentation.home

import com.juanpablo0612.carpool.presentation.booking.BookingError
import com.juanpablo0612.carpool.presentation.booking.asStringResource
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.StringResource

sealed class HomeError {
    data object LoadFailed : HomeError()
    data class BookingAction(val error: BookingError) : HomeError()

    fun asStringResource(): StringResource = when (this) {
        LoadFailed -> Res.string.error_unknown
        is BookingAction -> error.asStringResource()
    }
}
