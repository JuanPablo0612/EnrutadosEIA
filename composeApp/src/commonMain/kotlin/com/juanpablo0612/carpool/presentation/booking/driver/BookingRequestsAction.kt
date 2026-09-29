package com.juanpablo0612.carpool.presentation.booking.driver

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.presentation.booking.driver.decision.BookingDecisionAction

sealed class BookingRequestsAction {
    data class OnTabSelected(val tab: BookingRequestsTab) : BookingRequestsAction()
    data class OnTripClick(val tripId: String) : BookingRequestsAction()
    data class OnViewProfile(val passengerId: String) : BookingRequestsAction()
    data class OnMessagePassenger(val booking: Booking) : BookingRequestsAction()
    data class OnDecision(val action: BookingDecisionAction) : BookingRequestsAction()
    data object OnRetry : BookingRequestsAction()
}
