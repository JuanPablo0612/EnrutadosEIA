package com.juanpablo0612.carpool.presentation.trip.publish

import com.juanpablo0612.carpool.domain.place.model.Place
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

sealed class PublishTripAction {
    data class OnSavedRouteClick(val routeId: String) : PublishTripAction()
    data object OnClearLinkedRoute : PublishTripAction()

    data object OnOriginClick : PublishTripAction()
    data object OnDestinationClick : PublishTripAction()
    data object OnAddWaypointClick : PublishTripAction()
    data class OnEditWaypointClick(val index: Int) : PublishTripAction()
    data class OnRemoveWaypoint(val index: Int) : PublishTripAction()
    data class OnPlaceSelected(val place: Place) : PublishTripAction()
    data object OnCancelSelection : PublishTripAction()
    data object OnReverseStops : PublishTripAction()

    data object OnSelectToday : PublishTripAction()
    data object OnSelectTomorrow : PublishTripAction()
    data class OnDateSelected(val date: LocalDate) : PublishTripAction()
    data class OnTimeSelected(val time: LocalTime) : PublishTripAction()
    data object OnShowDatePicker : PublishTripAction()
    data object OnShowTimePicker : PublishTripAction()
    data object OnDismissDatePicker : PublishTripAction()
    data object OnDismissTimePicker : PublishTripAction()

    data class OnVehicleSelected(val vehicleId: String) : PublishTripAction()
    data object OnRegisterVehicleClick : PublishTripAction()
    data class OnSetSeats(val count: Int) : PublishTripAction()
    data class OnSetContribution(val pesos: Int?) : PublishTripAction()
    data class OnSetMessage(val text: String) : PublishTripAction()

    data class OnToggleSaveAsRoute(val enabled: Boolean) : PublishTripAction()
    data class OnRouteNameChange(val name: String) : PublishTripAction()
    data class OnToggleRecurringDay(val day: DayOfWeek) : PublishTripAction()

    data object OnReviewClick : PublishTripAction()
    data object OnConfirmPublish : PublishTripAction()
    data object OnDismissSummary : PublishTripAction()
    data object OnBackClick : PublishTripAction()
    data object OnConfirmDiscard : PublishTripAction()
    data object OnDismissDiscard : PublishTripAction()
}
