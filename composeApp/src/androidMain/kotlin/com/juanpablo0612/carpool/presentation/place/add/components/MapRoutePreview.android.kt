package com.juanpablo0612.carpool.presentation.place.add.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.juanpablo0612.carpool.domain.place.model.Coordinates

@Composable
actual fun MapRoutePreview(
    markers: List<Coordinates>,
    modifier: Modifier,
) {
    val latLngs = markers.map { LatLng(it.latitude, it.longitude) }
    val cameraState = rememberCameraPositionState()

    // Fits every stop in view instead of centering on just one — a single-point camera (as
    // MapPreview uses) would leave a multi-stop route mostly off-screen.
    LaunchedEffect(latLngs) {
        if (latLngs.isEmpty()) return@LaunchedEffect
        if (latLngs.size == 1) {
            cameraState.animate(CameraUpdateFactory.newLatLngZoom(latLngs.first(), 14f))
            return@LaunchedEffect
        }
        val bounds = LatLngBounds.Builder().apply {
            latLngs.forEach { include(it) }
        }.build()
        cameraState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 80))
    }

    GoogleMap(
        cameraPositionState = cameraState,
        modifier = modifier,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            scrollGesturesEnabled = false,
            zoomGesturesEnabled = false,
            rotationGesturesEnabled = false,
            tiltGesturesEnabled = false,
        ),
    ) {
        if (latLngs.size >= 2) {
            Polyline(
                points = latLngs,
                color = MaterialTheme.colorScheme.primary,
                width = 6f,
            )
        }
        latLngs.forEach { position ->
            Marker(state = rememberMarkerState(position = position))
        }
    }
}
