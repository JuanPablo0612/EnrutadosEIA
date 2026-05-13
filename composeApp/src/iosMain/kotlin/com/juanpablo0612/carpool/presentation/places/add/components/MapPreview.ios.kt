package com.juanpablo0612.carpool.presentation.places.add.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.domain.places.model.Coordinates

@Composable
actual fun MapPreview(
    coordinates: Coordinates?,
    onPinDragged: (Coordinates) -> Unit,
    modifier: Modifier,
) {
    Box(modifier = modifier)
}
