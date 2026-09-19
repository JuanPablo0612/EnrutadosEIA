package com.juanpablo0612.carpool.presentation.place.add.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.domain.place.model.Coordinates

// No-op on iOS, matching MapPreview.ios.kt's precedent: the iOS target is an inactive scaffold
// (see CLAUDE.md).
@Composable
actual fun MapRoutePreview(
    markers: List<Coordinates>,
    modifier: Modifier,
) {
    Box(modifier = modifier)
}
