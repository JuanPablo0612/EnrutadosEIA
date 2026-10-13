package com.juanpablo0612.carpool.presentation.place.add.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import com.juanpablo0612.carpool.domain.place.model.Coordinates
import org.koin.compose.koinInject
import platform.UIKit.UIColor

@Composable
actual fun MapRoutePreview(
    markers: List<Coordinates>,
    modifier: Modifier,
) {
    if (!hasMapsApiKey) {
        MapPlaceholder(modifier)
        return
    }
    val factory = koinInject<NativeMapViewFactory>()
    val map = remember(factory) { factory.createRouteMap() }
    val lineColor = MaterialTheme.colorScheme.primary

    UIKitView(
        factory = { map.view },
        modifier = modifier,
        update = { map.showRoute(markers, lineColor.toUIColor()) },
        // Read-only like on Android, so touches go to Compose and the screen keeps scrolling.
        properties = UIKitInteropProperties(isInteractive = false, isNativeAccessibilityEnabled = false),
    )
}

private fun Color.toUIColor(): UIColor = UIColor(
    red = red.toDouble(),
    green = green.toDouble(),
    blue = blue.toDouble(),
    alpha = alpha.toDouble(),
)
