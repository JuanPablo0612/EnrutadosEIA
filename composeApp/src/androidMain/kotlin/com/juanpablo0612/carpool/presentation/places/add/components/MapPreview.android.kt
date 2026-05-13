package com.juanpablo0612.carpool.presentation.places.add.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.DragState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.juanpablo0612.carpool.domain.places.model.Coordinates

@Composable
actual fun MapPreview(
    coordinates: Coordinates?,
    onPinDragged: (Coordinates) -> Unit,
    modifier: Modifier,
) {
    val cameraState = rememberCameraPositionState()
    val markerState = rememberMarkerState(
        position = coordinates?.let { LatLng(it.latitude, it.longitude) } ?: LatLng(0.0, 0.0)
    )

    LaunchedEffect(coordinates) {
        coordinates?.let {
            val latLng = LatLng(it.latitude, it.longitude)
            markerState.position = latLng
            cameraState.position = CameraPosition.fromLatLngZoom(latLng, 16f)
        }
    }

    LaunchedEffect(markerState.dragState) {
        if (markerState.dragState == DragState.END) {
            onPinDragged(Coordinates(markerState.position.latitude, markerState.position.longitude))
        }
    }

    GoogleMap(cameraPositionState = cameraState, modifier = modifier) {
        if (coordinates != null) {
            Marker(
                state = markerState,
                draggable = true,
            )
        }
    }
}
