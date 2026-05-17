package com.juanpablo0612.carpool.presentation.places.add.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.domain.places.model.Coordinates

@Composable
expect fun MapPreview(
    coordinates: Coordinates?,
    onPinDragged: (Coordinates) -> Unit,
    modifier: Modifier = Modifier,
    isMyLocationEnabled: Boolean = false,
)
