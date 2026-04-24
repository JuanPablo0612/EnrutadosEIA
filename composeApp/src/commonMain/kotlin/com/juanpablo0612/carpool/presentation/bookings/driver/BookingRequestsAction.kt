package com.juanpablo0612.carpool.presentation.bookings.driver

sealed class BookingRequestsAction {
    data class OnConfirmClick(val bookingId: String) : BookingRequestsAction()
    data class OnRejectClick(val bookingId: String) : BookingRequestsAction()
}
