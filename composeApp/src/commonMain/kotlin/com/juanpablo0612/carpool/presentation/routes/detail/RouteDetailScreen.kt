package com.juanpablo0612.carpool.presentation.routes.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.presentation.places.selector.PlaceSelectorAction
import com.juanpablo0612.carpool.presentation.places.selector.PlaceSelectorContent
import com.juanpablo0612.carpool.presentation.places.selector.PlaceSelectorUiState
import com.juanpablo0612.carpool.presentation.places.selector.PlaceSelectorViewModel
import com.juanpablo0612.carpool.presentation.routes.create.components.RouteStopItem
import com.juanpablo0612.carpool.presentation.routes.create.components.SectionHeader
import com.juanpablo0612.carpool.presentation.routes.create.components.StopType
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import enrutadoseia.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RouteDetailScreen(
    viewModel: RouteDetailViewModel,
    onBackClick: () -> Unit,
    onNavigateToAddPlace: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val placeSelectorViewModel: PlaceSelectorViewModel = koinViewModel()
    val placeSelectorState by placeSelectorViewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RouteDetailEvent.RouteUpdated -> onBackClick()
            RouteDetailEvent.NavigateBack -> onBackClick()
        }
    }

    RouteDetailScreenContent(
        state = state,
        placeSelectorState = placeSelectorState,
        onAction = viewModel::onAction,
        onPlaceSelectorAction = placeSelectorViewModel::onAction,
        onNavigateToAddPlace = onNavigateToAddPlace
    )
}

@Composable
fun RouteDetailScreenContent(
    state: RouteDetailUiState,
    placeSelectorState: PlaceSelectorUiState,
    onAction: (RouteDetailAction) -> Unit,
    onPlaceSelectorAction: (PlaceSelectorAction) -> Unit,
    onNavigateToAddPlace: () -> Unit
) {
    if (state.selectionTarget != null) {
        PlaceSelectorContent(
            state = placeSelectorState,
            onAction = onPlaceSelectorAction,
            onPlaceSelected = { place ->
                onAction(RouteDetailAction.OnPlaceSelectedFromResult(place))
            },
            onBack = {
                onAction(RouteDetailAction.OnCancelSelection)
            },
            onNavigateToAddPlace = onNavigateToAddPlace
        )
    } else {
        RouteDetailContent(
            state = state,
            onAction = onAction
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailContent(
    state: RouteDetailUiState,
    onAction: (RouteDetailAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.route_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = { onAction(RouteDetailAction.OnBackClick) }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_back_24px),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                SectionHeader(stringResource(Res.string.waypoints_section_title))
            }

            item {
                RouteStopItem(
                    label = stringResource(Res.string.origin_label),
                    place = state.origin,
                    type = StopType.START,
                    isLocked = false,
                    onClick = { onAction(RouteDetailAction.OnOriginClick) }
                )
            }

            itemsIndexed(state.waypoints) { index, waypoint ->
                RouteStopItem(
                    label = "Stop ${index + 1}",
                    place = waypoint,
                    type = StopType.MIDDLE,
                    isLocked = false,
                    onRemove = { onAction(RouteDetailAction.OnRemoveWaypoint(index)) },
                    onClick = { onAction(RouteDetailAction.OnEditWaypointClick(index)) }
                )
            }

            item {
                TextButton(
                    onClick = { onAction(RouteDetailAction.OnAddWaypointClick) },
                    modifier = Modifier.padding(horizontal = 40.dp)
                ) {
                    Icon(vectorResource(Res.drawable.add_24px), contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(Res.string.add_waypoint_button))
                }
            }

            item {
                RouteStopItem(
                    label = stringResource(Res.string.destination_label),
                    place = state.destination,
                    type = StopType.END,
                    isLocked = false,
                    showConnector = false,
                    onClick = { onAction(RouteDetailAction.OnDestinationClick) }
                )
            }

            item {
                state.error?.let { error ->
                    Text(
                        text = stringResource(error.asStringResource()),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            item {
                Button(
                    onClick = { onAction(RouteDetailAction.OnUpdateClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = !state.isSaving
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(Res.string.update_route_button))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun RouteDetailContentPreview() {
    CarpoolTheme {
        RouteDetailContent(
            state = RouteDetailUiState(
                isLoading = false,
                origin = Place(name = "Casa", address = "Calle 10 #20-30", latitude = 6.2, longitude = -75.6),
                destination = Place(name = "EIA", address = "Cl. 49 Sur #50-90", latitude = 6.18, longitude = -75.59),
                waypoints = listOf(
                    Place(name = "Parada 1", address = "Carrera 43A #1-50", latitude = 6.21, longitude = -75.57)
                )
            ),
            onAction = {}
        )
    }
}
