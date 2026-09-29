package com.juanpablo0612.carpool.presentation.booking.driver

import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.presentation.booking.driver.decision.BookingDecisionAction

sealed class TripPassengersAction {
    data class OnViewProfile(val passengerId: String) : TripPassengersAction()
    data class OnMessagePassenger(val booking: Booking) : TripPassengersAction()
    data class OnRatePassenger(val booking: Booking) : TripPassengersAction()
    data class OnDecision(val action: BookingDecisionAction) : TripPassengersAction()
    data object OnRetry : TripPassengersAction()
}
