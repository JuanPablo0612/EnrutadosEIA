package com.juanpablo0612.carpool.presentation.places.selector

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.presentation.places.selector.components.PlaceItem
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import enrutadoseia.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun PlaceSelectorScreen(
    viewModel: PlaceSelectorViewModel,
    onPlaceSelected: (Place) -> Unit,
    onBack: () -> Unit,
    onNavigateToAddPlace: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    PlaceSelectorContent(
        state = state,
        onAction = viewModel::onAction,
        onPlaceSelected = onPlaceSelected,
        onBack = onBack,
        onNavigateToAddPlace = onNavigateToAddPlace
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceSelectorContent(
    state: PlaceSelectorUiState,
    onAction: (PlaceSelectorAction) -> Unit,
    onPlaceSelected: (Place) -> Unit,
    onBack: () -> Unit,
    onNavigateToAddPlace: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.search_places)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_back_24px),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAddPlace) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.add_24px),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { onAction(PlaceSelectorAction.OnQueryChange(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(Res.string.search_places)) },
                leadingIcon = {
                    Icon(
                        imageVector = vectorResource(Res.drawable.search_24px),
                        contentDescription = null
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                val displayList = if (state.query.isBlank()) state.savedPlaces else state.searchResults
                if (displayList.isEmpty() && !state.isLoading) {
                    item {
                        Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (state.query.isBlank()) stringResource(Res.string.no_saved_places)
                                else stringResource(Res.string.no_results_found),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                items(displayList, key = { it.id }) { place ->
                    PlaceItem(
                        place = place,
                        onClick = { onPlaceSelected(place) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Preview
@Composable
private fun PlaceSelectorEmptyPreview() {
    CarpoolTheme {
        PlaceSelectorContent(
            state = PlaceSelectorUiState(),
            onAction = {},
            onPlaceSelected = {},
            onBack = {},
            onNavigateToAddPlace = {}
        )
    }
}

@Preview
@Composable
private fun PlaceSelectorWithSavedPlacesPreview() {
    CarpoolTheme {
        PlaceSelectorContent(
            state = PlaceSelectorUiState(
                savedPlaces = listOf(
                    Place(id = "1", name = "Casa", address = "Calle 10 #20-30", latitude = 6.2, longitude = -75.6),
                    Place(id = "2", name = "Oficina", address = "Carrera 43A #1-50", latitude = 6.21, longitude = -75.57)
                )
            ),
            onAction = {},
            onPlaceSelected = {},
            onBack = {},
            onNavigateToAddPlace = {}
        )
    }
}
