package com.juanpablo0612.carpool.presentation.place.add.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.domain.place.model.Coordinates

@Composable
expect fun MapPreview(
    coordinates: Coordinates?,
    onPinDragged: (Coordinates) -> Unit,
    modifier: Modifier = Modifier,
    isMyLocationEnabled: Boolean = false,
)
