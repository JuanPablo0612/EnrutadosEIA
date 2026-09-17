package com.juanpablo0612.carpool.presentation.booking.driver

import com.juanpablo0612.carpool.domain.booking.model.RejectReason

sealed class TripPassengersAction {
    data class Accept(val bookingId: String, val tripId: String) : TripPassengersAction()
    data class OpenReject(val bookingId: String) : TripPassengersAction()
    data class SelectRejectReason(val reason: RejectReason) : TripPassengersAction()
    data class UpdateRejectComment(val comment: String) : TripPassengersAction()
    data class ConfirmReject(val bookingId: String) : TripPassengersAction()
    data object DismissReject : TripPassengersAction()
    data class OpenCancelConfirmed(val bookingId: String) : TripPassengersAction()
    data object DismissCancelConfirmed : TripPassengersAction()
    data class CancelConfirmed(val bookingId: String) : TripPassengersAction()
    data class OpenPassengerProfile(val passengerId: String) : TripPassengersAction()
    data class OnRateBooking(
        val bookingId: String,
        val tripId: String,
        val rateeId: String,
        val rateeName: String
    ) : TripPassengersAction()
    data object DismissError : TripPassengersAction()
}
