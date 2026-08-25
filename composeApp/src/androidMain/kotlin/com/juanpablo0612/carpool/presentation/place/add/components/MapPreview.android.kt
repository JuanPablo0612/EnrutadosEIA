package com.juanpablo0612.carpool.presentation.place.add.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.juanpablo0612.carpool.domain.place.model.Coordinates
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop

@Composable
actual fun MapPreview(
    coordinates: Coordinates?,
    onPinDragged: (Coordinates) -> Unit,
    modifier: Modifier,
    isMyLocationEnabled: Boolean,
) {
    val cameraState = rememberCameraPositionState {
        coordinates?.let {
            position = CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 16f)
        }
    }

    val markerState = rememberUpdatedMarkerState(
        position = coordinates?.let { LatLng(it.latitude, it.longitude) } ?: LatLng(0.0, 0.0)
    )

    LaunchedEffect(coordinates) {
        coordinates?.let {
            val latLng = LatLng(it.latitude, it.longitude)
            if (cameraState.position.target != latLng) {
                cameraState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
            }
        }
    }

    LaunchedEffect(markerState) {
        snapshotFlow { markerState.isDragging }
            .drop(1)
            .collectLatest { isDragging ->
                if (!isDragging) {
                    onPinDragged(
                        Coordinates(
                            markerState.position.latitude,
                            markerState.position.longitude
                        )
                    )
                }
            }
    }

    GoogleMap(
        cameraPositionState = cameraState,
        modifier = modifier,
        properties = MapProperties(isMyLocationEnabled = isMyLocationEnabled),
        uiSettings = MapUiSettings(myLocationButtonEnabled = false),
    ) {
        if (coordinates != null) {
            Marker(
                state = markerState,
                draggable = true,
            )
        }
    }
}
