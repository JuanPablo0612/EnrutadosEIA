package com.juanpablo0612.carpool.presentation.routes.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun CreateRouteScreen(
    viewModel: CreateRouteViewModel,
    onBackClick: () -> Unit,
    onRouteCreated: () -> Unit,
    onNavigateToAddPlace: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val placeSelectorViewModel: PlaceSelectorViewModel = koinViewModel()
    val placeSelectorState by placeSelectorViewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            CreateRouteEvent.NavigateBack -> onBackClick()
            CreateRouteEvent.RouteCreated -> onRouteCreated()
        }
    }

    CreateRouteScreenContent(
        state = state,
        placeSelectorState = placeSelectorState,
        onAction = viewModel::onAction,
        onPlaceSelectorAction = placeSelectorViewModel::onAction,
        onNavigateToAddPlace = onNavigateToAddPlace
    )
}

@Composable
fun CreateRouteScreenContent(
    state: CreateRouteUiState,
    placeSelectorState: PlaceSelectorUiState,
    onAction: (CreateRouteAction) -> Unit,
    onPlaceSelectorAction: (PlaceSelectorAction) -> Unit,
    onNavigateToAddPlace: () -> Unit = {}
) {
    if (state.selectionTarget != null) {
        PlaceSelectorContent(
            state = placeSelectorState,
            onAction = onPlaceSelectorAction,
            onPlaceSelected = { place ->
                onAction(CreateRouteAction.OnPlaceSelectedFromResult(place))
            },
            onBack = {
                onAction(CreateRouteAction.OnCancelSelection)
            },
            onNavigateToAddPlace = onNavigateToAddPlace
        )
    } else {
        CreateRouteContent(
            state = state,
            onAction = onAction
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRouteContent(
    state: CreateRouteUiState,
    onAction: (CreateRouteAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.create_route_title)) },
                navigationIcon = {
                    IconButton(onClick = { onAction(CreateRouteAction.OnBackClick) }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_back_24px),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
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
                    onClick = { onAction(CreateRouteAction.OnOriginClick) }
                )
            }

            itemsIndexed(state.waypoints) { index, waypoint ->
                RouteStopItem(
                    label = "Stop ${index + 1}",
                    place = waypoint,
                    type = StopType.MIDDLE,
                    isLocked = false,
                    onRemove = { onAction(CreateRouteAction.OnRemoveWaypoint(index)) },
                    onClick = { onAction(CreateRouteAction.OnEditWaypointClick(index)) }
                )
            }

            item {
                TextButton(
                    onClick = { onAction(CreateRouteAction.OnAddWaypointClick) },
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
                    onClick = { onAction(CreateRouteAction.OnDestinationClick) }
                )
            }

            item {
                if (state.error != null) {
                    Text(
                        text = stringResource(state.error.asStringResource()),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            item {
                Button(
                    onClick = { onAction(CreateRouteAction.OnSaveClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(Res.string.save_route_button))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun CreateRouteContentPreview() {
    CarpoolTheme {
        CreateRouteContent(
            state = CreateRouteUiState(
                origin = Place(
                    name = "Home",
                    address = "123 Main St",
                    latitude = 0.0,
                    longitude = 0.0
                ),
                waypoints = listOf(
                    Place(name = "Stop 1", address = "Address 1", latitude = 0.0, longitude = 0.0)
                )
            ),
            onAction = {}
        )
    }
}
