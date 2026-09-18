package com.juanpablo0612.carpool.presentation.place.add.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.domain.place.model.Coordinates
import com.juanpablo0612.carpool.domain.place.model.MapPointOfInterest

@Composable
actual fun MapPreview(
    coordinates: Coordinates?,
    onPinDragged: (Coordinates) -> Unit,
    modifier: Modifier,
    isMyLocationEnabled: Boolean,
    onPoiSelected: (MapPointOfInterest) -> Unit,
) {
    Box(modifier = modifier)
}
