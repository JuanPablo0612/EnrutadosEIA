package com.juanpablo0612.carpool.presentation.home

/**
 * What Inicio offers someone still finding their way around. Riding and driving are both
 * optional, so nothing here reads as a checklist: a new account picks how to start, and a rider
 * without a car hears once, dismissibly, that they could drive too.
 */
sealed class HomeWelcome {
    /** A new account with nothing going on: choose between finding a seat and registering a car. */
    data object ChooseHowToStart : HomeWelcome()

    /** Someone who has asked for a seat but has no car: a hint they can hide for good. */
    data object SuggestVehicle : HomeWelcome()

    data object None : HomeWelcome()

    companion object {
        fun of(
            hasBookedBefore: Boolean,
            hasVehicles: Boolean,
            hasUpcomingTrip: Boolean,
            vehicleSuggestionDismissed: Boolean,
        ): HomeWelcome = when {
            // Searching is always one tap away on Inicio, so a driver is never nudged to ride.
            hasVehicles -> None
            hasBookedBefore -> if (vehicleSuggestionDismissed) None else SuggestVehicle
            hasUpcomingTrip -> None
            else -> ChooseHowToStart
        }
    }
}
