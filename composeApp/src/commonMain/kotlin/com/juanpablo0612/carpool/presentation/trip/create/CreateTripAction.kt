package com.juanpablo0612.carpool.presentation.trip.create

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

sealed class CreateTripAction {
    data class OnVehicleSelected(val vehicleId: String) : CreateTripAction()
    data class OnDateSelected(val date: LocalDate) : CreateTripAction()
    data class OnTimeSelected(val time: LocalTime) : CreateTripAction()
    data object OnShowDatePicker : CreateTripAction()
    data object OnShowTimePicker : CreateTripAction()
    data object OnDismissDatePicker : CreateTripAction()
    data object OnDismissTimePicker : CreateTripAction()
    data object OnPublishClick : CreateTripAction()
    data object OnBackClick : CreateTripAction()
}
