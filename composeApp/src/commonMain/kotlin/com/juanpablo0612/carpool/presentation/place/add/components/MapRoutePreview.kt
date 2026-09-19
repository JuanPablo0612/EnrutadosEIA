package com.juanpablo0612.carpool.presentation.place.add.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.domain.place.model.Coordinates

/**
 * Read-only map showing a route's stops (origin, waypoints, destination) as pins connected by a
 * line, with the camera framed to fit all of them. Unlike [MapPreview] (single draggable pin,
 * POI-tappable, used for picking one location), this has no interaction beyond viewing — there's
 * no drag/POI-click use case for "here's the route a trip already follows."
 *
 * [markers] is ordered start-to-end; fewer than 2 entries still renders (just the pin(s), no line).
 */
@Composable
expect fun MapRoutePreview(
    markers: List<Coordinates>,
    modifier: Modifier = Modifier,
)
