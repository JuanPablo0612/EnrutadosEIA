package com.juanpablo0612.carpool.presentation.trip.tracking.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.domain.place.model.Coordinates
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.presentation.place.add.components.MapPreview
import com.juanpablo0612.carpool.presentation.place.add.components.MapRoutePreview

/**
 * The tracking map: centred on the driver once their position is known, and the trip's stops
 * until then, so the screen never opens on an empty map.
 */
@Composable
internal fun TripTrackingMap(
    trip: Trip?,
    driverPosition: Coordinates?,
    modifier: Modifier = Modifier,
) {
    if (driverPosition != null) {
        MapPreview(
            coordinates = driverPosition,
            // Read-only: the position comes from the driver's device, so a dragged pin is discarded.
            onPinDragged = {},
            modifier = modifier,
        )
    } else {
        val stops = remember(trip) {
            trip?.let { listOf(it.origin) + it.waypoints + it.destination }
                .orEmpty()
                .map { Coordinates(it.latitude, it.longitude) }
        }
        MapRoutePreview(markers = stops, modifier = modifier)
    }
}
