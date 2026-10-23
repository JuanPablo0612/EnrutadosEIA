package com.juanpablo0612.carpool.presentation.trip.publishweek

import kotlinx.datetime.LocalDate

sealed class PublishWeekAction {
    data class OnToggleDay(val date: LocalDate) : PublishWeekAction()
    data class OnVehicleSelected(val vehicleId: String) : PublishWeekAction()
    data class OnSetSeats(val count: Int) : PublishWeekAction()
    data class OnSetContribution(val pesos: Int?) : PublishWeekAction()
    data class OnSetMessage(val text: String) : PublishWeekAction()
    data object OnRegisterVehicleClick : PublishWeekAction()
    data class OnVehicleRegistered(val vehicleId: String) : PublishWeekAction()
    data object OnEditRouteClick : PublishWeekAction()
    data object OnReviewClick : PublishWeekAction()
    data object OnConfirmPublish : PublishWeekAction()
    data object OnDismissSummary : PublishWeekAction()
    data object OnBackClick : PublishWeekAction()
    data object OnRetry : PublishWeekAction()
}

sealed class PublishWeekEvent {
    data object TripsPublished : PublishWeekEvent()
    data object NavigateBack : PublishWeekEvent()
    data object NavigateToRegisterVehicle : PublishWeekEvent()
    data class NavigateToRouteDetail(val routeId: String) : PublishWeekEvent()
}
