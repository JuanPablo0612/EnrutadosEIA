package com.juanpablo0612.carpool.presentation.route.search

import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.CampusDirection

/** A one-tap search from outside Buscar, e.g. Inicio's "A Las Palmas" chip. */
data class SearchShortcut(val campus: Place, val direction: CampusDirection)
