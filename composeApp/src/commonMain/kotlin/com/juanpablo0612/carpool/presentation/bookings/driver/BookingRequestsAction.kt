package com.juanpablo0612.carpool.presentation.bookings.driver

import com.juanpablo0612.carpool.domain.booking.model.RejectReason

sealed class BookingRequestsAction {
    data class SelectTab(val tab: BookingsTab) : BookingRequestsAction()
    data class Accept(val bookingId: String, val tripId: String) : BookingRequestsAction()
    data class OpenReject(val bookingId: String) : BookingRequestsAction()
    data class SelectRejectReason(val reason: RejectReason) : BookingRequestsAction()
    data class UpdateRejectComment(val comment: String) : BookingRequestsAction()
    data class ConfirmReject(val bookingId: String) : BookingRequestsAction()
    data object DismissReject : BookingRequestsAction()
    data class OpenCancelConfirmed(val bookingId: String) : BookingRequestsAction()
    data object DismissCancelConfirmed : BookingRequestsAction()
    data class CancelConfirmed(val bookingId: String) : BookingRequestsAction()
    data class OpenPassengerProfile(val passengerId: String) : BookingRequestsAction()
    data object Refresh : BookingRequestsAction()
    data object DismissSnackbar : BookingRequestsAction()
}
