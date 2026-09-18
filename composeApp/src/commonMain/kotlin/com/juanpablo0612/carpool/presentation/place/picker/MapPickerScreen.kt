package com.juanpablo0612.carpool.presentation.place.picker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.place.model.Coordinates
import com.juanpablo0612.carpool.domain.place.model.MapPointOfInterest
import com.juanpablo0612.carpool.presentation.place.add.components.MapPreview
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.map_picker_address_unavailable
import enrutadoseia.composeapp.generated.resources.map_picker_confirm
import enrutadoseia.composeapp.generated.resources.map_picker_my_location
import enrutadoseia.composeapp.generated.resources.map_picker_resolving_address
import enrutadoseia.composeapp.generated.resources.map_picker_title
import enrutadoseia.composeapp.generated.resources.my_location_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun MapPickerScreen(
    viewModel: MapPickerViewModel,
    onCoordinatesPicked: (Double, Double, String?) -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    MapPickerContent(
        state = state,
        onPinDragged = viewModel::onPinDragged,
        onPoiSelected = viewModel::onPoiSelected,
        onMyLocationClick = viewModel::onMyLocationClick,
        onConfirm = {
            onCoordinatesPicked(
                state.pickedCoordinates.latitude,
                state.pickedCoordinates.longitude,
                state.pickedPlaceName,
            )
        },
        onBack = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerContent(
    state: MapPickerUiState,
    onPinDragged: (Coordinates) -> Unit,
    onPoiSelected: (MapPointOfInterest) -> Unit,
    onMyLocationClick: () -> Unit,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            CarpoolBackTopBar(
                title = stringResource(Res.string.map_picker_title),
                onBack = onBack,
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenHorizontalForm, vertical = Spacing.lg),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (state.isResolvingAddress) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(Spacing.sm))
                        Text(
                            text = stringResource(Res.string.map_picker_resolving_address),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        Text(
                            text = state.resolvedAddress ?: stringResource(Res.string.map_picker_address_unavailable),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Spacer(Modifier.height(Spacing.sm))
                PrimaryButton(
                    text = stringResource(Res.string.map_picker_confirm),
                    onClick = onConfirm,
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            MapPreview(
                coordinates = state.pickedCoordinates,
                onPinDragged = onPinDragged,
                onPoiSelected = onPoiSelected,
                isMyLocationEnabled = state.isMyLocationEnabled,
                modifier = Modifier.fillMaxSize(),
            )

            FloatingActionButton(
                onClick = onMyLocationClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(Spacing.lg),
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                if (state.isLoadingLocation) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp), // icon-intrinsic size
                        strokeWidth = 2.dp, // hairline
                    )
                } else {
                    Icon(
                        vectorResource(Res.drawable.my_location_24px),
                        contentDescription = stringResource(Res.string.map_picker_my_location),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun MapPickerContentPreview() {
    CarpoolTheme {
        MapPickerContent(
            state = MapPickerUiState(pickedCoordinates = Coordinates(6.2, -75.6)),
            onPinDragged = {},
            onPoiSelected = {},
            onMyLocationClick = {},
            onConfirm = {},
            onBack = {},
        )
    }
}
