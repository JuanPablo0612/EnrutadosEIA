package com.juanpablo0612.carpool.presentation.trip.create

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

sealed class CreateTripAction {
    data class OnVehicleSelected(val vehicleId: String) : CreateTripAction()
    data object OnSelectTodayDate : CreateTripAction()
    data object OnSelectTomorrowDate : CreateTripAction()
    data class OnDateSelected(val date: LocalDate) : CreateTripAction()
    data class OnTimeSelected(val time: LocalTime) : CreateTripAction()
    data object OnShowDatePicker : CreateTripAction()
    data object OnShowTimePicker : CreateTripAction()
    data object OnDismissDatePicker : CreateTripAction()
    data object OnDismissTimePicker : CreateTripAction()
    data class OnSetSeats(val count: Int) : CreateTripAction()
    data class OnSetContribution(val pesos: Int?) : CreateTripAction()
    data class OnSetMessage(val text: String) : CreateTripAction()
    data object OnPublishClick : CreateTripAction()
    data object OnNavigateToRegisterVehicle : CreateTripAction()
    data object OnNavigateToVehiclesList : CreateTripAction()
    data object OnBackClick : CreateTripAction()
}
