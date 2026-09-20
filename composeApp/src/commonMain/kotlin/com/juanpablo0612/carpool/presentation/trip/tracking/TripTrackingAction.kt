package com.juanpablo0612.carpool.presentation.trip.tracking

sealed class TripTrackingAction {
    data class OnMarkPickedUp(val passengerId: String) : TripTrackingAction()
    data class OnMarkDroppedOffClick(val passengerId: String) : TripTrackingAction()
    data object OnConfirmDropOff : TripTrackingAction()
    data object OnDismissDropOffConfirm : TripTrackingAction()
    data object OnCompleteTripClick : TripTrackingAction()
    data object OnCompleteTripConfirm : TripTrackingAction()
    data object OnCompleteTripDismiss : TripTrackingAction()
    data object OnSOSClick : TripTrackingAction()
    data object OnSOSDismiss : TripTrackingAction()
    data object OnSOSCallEmergencyClick : TripTrackingAction()
    data object OnSOSShareLocationClick : TripTrackingAction()
    data object OnSOSAddEmergencyContactClick : TripTrackingAction()
    data object OnBackClick : TripTrackingAction()
    data class OnChatClick(val bookingId: String, val otherPartyName: String) : TripTrackingAction()
    data object OnErrorDismissed : TripTrackingAction()
}
