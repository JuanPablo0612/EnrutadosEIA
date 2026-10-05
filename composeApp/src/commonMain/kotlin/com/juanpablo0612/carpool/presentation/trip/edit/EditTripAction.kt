package com.juanpablo0612.carpool.presentation.trip.edit

import com.juanpablo0612.carpool.domain.place.model.Place

sealed class EditTripAction {
    data object OnBackClick : EditTripAction()
    data object OnConfirmDiscard : EditTripAction()
    data object OnDismissDiscard : EditTripAction()
    data object OnRetry : EditTripAction()
    data class OnSetSeats(val seats: Int) : EditTripAction()
    data class OnEditWaypointClick(val index: Int) : EditTripAction()
    data class OnRemoveWaypoint(val index: Int) : EditTripAction()
    data object OnAddWaypointClick : EditTripAction()
    data class OnPlaceSelected(val place: Place) : EditTripAction()
    data object OnCancelSelection : EditTripAction()
    data object OnSaveClick : EditTripAction()
}
