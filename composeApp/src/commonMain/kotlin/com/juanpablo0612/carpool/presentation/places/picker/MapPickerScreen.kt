package com.juanpablo0612.carpool.presentation.places.picker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.places.add.components.MapPreview
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.map_picker_confirm
import enrutadoseia.composeapp.generated.resources.map_picker_my_location
import enrutadoseia.composeapp.generated.resources.map_picker_title
import enrutadoseia.composeapp.generated.resources.my_location_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerScreen(
    viewModel: MapPickerViewModel,
    onCoordinatesPicked: (Double, Double) -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CarpoolBackTopBar(
                title = stringResource(Res.string.map_picker_title),
                onBack = onBack,
            )
        },
        bottomBar = {
            PrimaryButton(
                text = stringResource(Res.string.map_picker_confirm),
                onClick = {
                    onCoordinatesPicked(
                        state.pickedCoordinates.latitude,
                        state.pickedCoordinates.longitude,
                    )
                },
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            MapPreview(
                coordinates = state.pickedCoordinates,
                onPinDragged = { viewModel.onPinDragged(it) },
                isMyLocationEnabled = state.isMyLocationEnabled,
                modifier = Modifier.fillMaxSize(),
            )

            FloatingActionButton(
                onClick = { viewModel.onMyLocationClick() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                if (state.isLoadingLocation) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
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
