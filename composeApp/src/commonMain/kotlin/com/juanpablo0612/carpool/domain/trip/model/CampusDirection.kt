package com.juanpablo0612.carpool.domain.trip.model

/**
 * Every trip in the community starts or ends at an EIA campus, so a search is "to" or "from"
 * one. The passenger names the campus and their own place; the direction decides which end is
 * which.
 */
sealed class CampusDirection {
    /** From the passenger's place to the campus. */
    data object ToCampus : CampusDirection()

    /** From the campus to the passenger's place. */
    data object FromCampus : CampusDirection()
}
