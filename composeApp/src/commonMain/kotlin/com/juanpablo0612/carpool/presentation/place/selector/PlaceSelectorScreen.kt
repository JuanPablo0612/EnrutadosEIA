package com.juanpablo0612.carpool.presentation.place.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.presentation.place.selector.components.PlaceRow
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_24px
import enrutadoseia.composeapp.generated.resources.cancel_button
import enrutadoseia.composeapp.generated.resources.cd_add_place
import enrutadoseia.composeapp.generated.resources.cd_delete_place
import enrutadoseia.composeapp.generated.resources.delete_24px
import enrutadoseia.composeapp.generated.resources.delete_place_confirm_body
import enrutadoseia.composeapp.generated.resources.delete_place_confirm_title
import enrutadoseia.composeapp.generated.resources.location_on_24px
import enrutadoseia.composeapp.generated.resources.place_selector_allow_location
import enrutadoseia.composeapp.generated.resources.place_selector_current_location
import enrutadoseia.composeapp.generated.resources.place_selector_no_results
import enrutadoseia.composeapp.generated.resources.place_selector_resolving_location
import enrutadoseia.composeapp.generated.resources.place_selector_search_hint
import enrutadoseia.composeapp.generated.resources.place_selector_section_eia
import enrutadoseia.composeapp.generated.resources.place_selector_section_my_places
import enrutadoseia.composeapp.generated.resources.place_selector_section_results
import enrutadoseia.composeapp.generated.resources.place_selector_title_destination
import enrutadoseia.composeapp.generated.resources.place_selector_title_my_places
import enrutadoseia.composeapp.generated.resources.place_selector_title_origin
import enrutadoseia.composeapp.generated.resources.place_selector_title_waypoint
import enrutadoseia.composeapp.generated.resources.route_delete_confirm_button
import enrutadoseia.composeapp.generated.resources.search_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun PlaceSelectorScreen(
    viewModel: PlaceSelectorViewModel,
    onPlaceSelected: (Place) -> Unit,
    onBack: () -> Unit,
    onNavigateToAddPlace: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is PlaceSelectorEvent.PlaceSelected -> onPlaceSelected(event.place)
            PlaceSelectorEvent.NavigateToAddPlace -> onNavigateToAddPlace()
            PlaceSelectorEvent.Dismiss -> onBack()
        }
    }

    PlaceSelectorContent(
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
        onPlaceSelected = onPlaceSelected,
        onNavigateToAddPlace = onNavigateToAddPlace,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceSelectorContent(
    state: PlaceSelectorUiState,
    onAction: (PlaceSelectorAction) -> Unit,
    onBack: () -> Unit,
    onPlaceSelected: (Place) -> Unit = {},
    onNavigateToAddPlace: () -> Unit = {},
) {
    val title = when (state.mode) {
        PlaceSelectorMode.Origin -> stringResource(Res.string.place_selector_title_origin)
        PlaceSelectorMode.Destination -> stringResource(Res.string.place_selector_title_destination)
        PlaceSelectorMode.Waypoint -> stringResource(Res.string.place_selector_title_waypoint)
        PlaceSelectorMode.MyPlaces -> stringResource(Res.string.place_selector_title_my_places)
    }

    Scaffold(
        topBar = {
            CarpoolBackTopBar(
                title = title,
                onBack = onBack,
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { onAction(PlaceSelectorAction.OnQueryChange(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm),
                placeholder = { Text(stringResource(Res.string.place_selector_search_hint)) },
                leadingIcon = {
                    Icon(vectorResource(Res.drawable.search_24px), contentDescription = null)
                },
                singleLine = true,
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                // Current location row — always visible
                item {
                    when {
                        state.isResolvingLocation -> {
                            ListItem(
                                headlineContent = { Text(stringResource(Res.string.place_selector_resolving_location)) },
                                leadingContent = { CircularProgressIndicator(modifier = Modifier.padding(Spacing.xs)) },
                            )
                        }
                        !state.locationPermissionGranted -> {
                            PlaceRow(
                                icon = vectorResource(Res.drawable.location_on_24px),
                                name = stringResource(Res.string.place_selector_current_location),
                                address = null,
                                trailing = {
                                    TextButton(onClick = { onAction(PlaceSelectorAction.RequestLocationPermission) }) {
                                        Text(stringResource(Res.string.place_selector_allow_location))
                                    }
                                },
                                onClick = {},
                            )
                        }
                        else -> {
                            PlaceRow(
                                icon = vectorResource(Res.drawable.location_on_24px),
                                name = stringResource(Res.string.place_selector_current_location),
                                address = state.currentLocation?.address,
                                onClick = { onAction(PlaceSelectorAction.UseCurrentLocation) },
                            )
                        }
                    }
                    HorizontalDivider()
                }

                // My places and EIA sections — hidden while searching
                if (state.searchQuery.isBlank()) {
                    item {
                        SectionHeader(
                            title = stringResource(Res.string.place_selector_section_my_places),
                            action = {
                                IconButton(onClick = onNavigateToAddPlace) {
                                    Icon(
                                        vectorResource(Res.drawable.add_24px),
                                        contentDescription = stringResource(Res.string.cd_add_place),
                                    )
                                }
                            }
                        )
                    }
                    items(state.savedPlaces, key = { it.id }) { place ->
                        PlaceRow(
                            icon = vectorResource(Res.drawable.location_on_24px),
                            name = place.name,
                            address = place.address,
                            trailing = {
                                IconButton(onClick = { onAction(PlaceSelectorAction.OnDeletePlace(place)) }) {
                                    Icon(
                                        vectorResource(Res.drawable.delete_24px),
                                        contentDescription = stringResource(Res.string.cd_delete_place),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            },
                            onClick = { onPlaceSelected(place) },
                        )
                        HorizontalDivider()
                    }

                    item {
                        SectionHeader(title = stringResource(Res.string.place_selector_section_eia))
                    }
                    items(state.campusPlaces, key = { it.id }) { place ->
                        PlaceRow(
                            icon = vectorResource(Res.drawable.location_on_24px),
                            name = place.name,
                            address = place.address,
                            onClick = { onPlaceSelected(place) },
                        )
                        HorizontalDivider()
                    }
                }

                // Search results section
                if (state.searchQuery.isNotBlank()) {
                    item {
                        SectionHeader(title = stringResource(Res.string.place_selector_section_results))
                    }
                    when {
                        state.isSearching -> {
                            item {
                                CircularProgressIndicator(modifier = Modifier.padding(Spacing.lg))
                            }
                        }
                        state.searchResults.isEmpty() -> {
                            item {
                                Text(
                                    text = stringResource(Res.string.place_selector_no_results),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.md),
                                )
                            }
                        }
                        else -> {
                            items(state.searchResults, key = { "${it.latitude}_${it.longitude}" }) { suggestion ->
                                PlaceRow(
                                    icon = vectorResource(Res.drawable.location_on_24px),
                                    name = suggestion.primaryText,
                                    address = suggestion.fullAddress,
                                    onClick = { onAction(PlaceSelectorAction.OnSuggestionSelected(suggestion)) },
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    state.isConfirmingDelete?.let { place ->
        AlertDialog(
            onDismissRequest = { onAction(PlaceSelectorAction.OnCancelDelete) },
            title = { Text(stringResource(Res.string.delete_place_confirm_title)) },
            text = { Text(stringResource(Res.string.delete_place_confirm_body, place.name)) },
            confirmButton = {
                TextButton(onClick = { onAction(PlaceSelectorAction.OnConfirmDelete) }) {
                    Text(stringResource(Res.string.route_delete_confirm_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(PlaceSelectorAction.OnCancelDelete) }) {
                    Text(stringResource(Res.string.cancel_button))
                }
            },
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    action: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
        )
        action?.invoke()
    }
}

@Preview
@Composable
private fun PlaceSelectorContentPreview() {
    CarpoolTheme {
        PlaceSelectorContent(
            state = PlaceSelectorUiState(
                mode = PlaceSelectorMode.Origin,
                savedPlaces = listOf(
                    Place(id = "1", name = "Casa", address = "Calle 10 #20-30", latitude = 6.2, longitude = -75.6)
                ),
                locationPermissionGranted = true,
                currentLocation = Place(name = "Ubicación actual", address = "Cra 43A", latitude = 6.2, longitude = -75.6)
            ),
            onAction = {},
            onBack = {},
        )
    }
}
