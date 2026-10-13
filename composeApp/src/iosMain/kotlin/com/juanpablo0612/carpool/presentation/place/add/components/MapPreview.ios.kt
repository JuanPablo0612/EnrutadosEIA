package com.juanpablo0612.carpool.presentation.place.add.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import com.juanpablo0612.carpool.domain.place.model.Coordinates
import com.juanpablo0612.carpool.domain.place.model.MapPointOfInterest
import org.koin.compose.koinInject

@Composable
actual fun MapPreview(
    coordinates: Coordinates?,
    onPinDragged: (Coordinates) -> Unit,
    modifier: Modifier,
    isMyLocationEnabled: Boolean,
    onPoiSelected: (MapPointOfInterest) -> Unit,
) {
    if (!hasMapsApiKey) {
        MapPlaceholder(modifier)
        return
    }
    val factory = koinInject<NativeMapViewFactory>()
    val currentOnPinDragged by rememberUpdatedState(onPinDragged)
    val currentOnPoiSelected by rememberUpdatedState(onPoiSelected)
    val map = remember(factory) {
        factory.createPlaceMap(
            object : NativePlaceMapListener {
                override fun onPinDragged(coordinates: Coordinates) = currentOnPinDragged(coordinates)
                override fun onPoiTapped(poi: MapPointOfInterest) = currentOnPoiSelected(poi)
            }
        )
    }

    // The default (cooperative) touch handling lets a quick swipe scroll a parent column while a
    // held touch pans the map.
    UIKitView(
        factory = { map.view },
        modifier = modifier,
        update = {
            if (coordinates == null) map.hidePin() else map.showPin(coordinates)
            map.showMyLocation(isMyLocationEnabled)
        },
    )
}
