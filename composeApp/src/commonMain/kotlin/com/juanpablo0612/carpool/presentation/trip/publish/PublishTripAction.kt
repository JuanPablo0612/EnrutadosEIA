package com.juanpablo0612.carpool.presentation.trip.publish

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

sealed class PublishTripAction {
    data class OnVehicleSelected(val vehicleId: String) : PublishTripAction()
    data object OnSelectTodayDate : PublishTripAction()
    data object OnSelectTomorrowDate : PublishTripAction()
    data class OnDateSelected(val date: LocalDate) : PublishTripAction()
    data class OnTimeSelected(val time: LocalTime) : PublishTripAction()
    data object OnShowDatePicker : PublishTripAction()
    data object OnShowTimePicker : PublishTripAction()
    data object OnDismissDatePicker : PublishTripAction()
    data object OnDismissTimePicker : PublishTripAction()
    data class OnSetSeats(val count: Int) : PublishTripAction()
    data class OnSetContribution(val pesos: Int?) : PublishTripAction()
    data class OnSetMessage(val text: String) : PublishTripAction()
    data object OnPublishClick : PublishTripAction()
    data object OnConfirmPublish : PublishTripAction()
    data object OnDismissPublishConfirm : PublishTripAction()
    data object OnNavigateToRegisterVehicle : PublishTripAction()
    data object OnNavigateToVehiclesList : PublishTripAction()
    data object OnBackClick : PublishTripAction()
}
