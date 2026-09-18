package com.juanpablo0612.carpool.presentation.route.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorAction
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorContent
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorEvent
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorUiState
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorViewModel
import com.juanpablo0612.carpool.presentation.route.create.components.DaySelector
import com.juanpablo0612.carpool.presentation.route.create.components.RouteStopItem
import com.juanpablo0612.carpool.presentation.route.create.components.SectionHeader
import com.juanpablo0612.carpool.presentation.route.create.components.StopType
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.components.TimePickerDialog
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.*
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateRouteScreen(
    viewModel: CreateRouteViewModel,
    onBackClick: () -> Unit,
    onRouteCreated: () -> Unit,
    onNavigateToAddPlace: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    // Each selection target gets its own keyed instance so the selector's mode-dependent title
    // and state don't bleed across origin/destination/waypoint (they used to share one instance).
    val originSelectorViewModel: PlaceSelectorViewModel = koinViewModel(key = "origin") { parametersOf("ORIGIN") }
    val destinationSelectorViewModel: PlaceSelectorViewModel = koinViewModel(key = "destination") { parametersOf("DESTINATION") }
    val waypointSelectorViewModel: PlaceSelectorViewModel = koinViewModel(key = "waypoint") { parametersOf("WAYPOINT") }
    val originSelectorState by originSelectorViewModel.state.collectAsState()
    val destinationSelectorState by destinationSelectorViewModel.state.collectAsState()
    val waypointSelectorState by waypointSelectorViewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            CreateRouteEvent.NavigateBack -> onBackClick()
            CreateRouteEvent.RouteCreated -> onRouteCreated()
        }
    }

    // The selector is an inline content swap, not a real back-stack entry — without this, system
    // back while it's open exits the whole create-route flow and discards the in-progress draft.
    BackHandler(enabled = state.selectionTarget != null) {
        viewModel.onAction(CreateRouteAction.OnCancelSelection)
    }

    val (activeSelectorState, activeSelectorViewModel) = when (state.selectionTarget) {
        SelectionTarget.Origin -> originSelectorState to originSelectorViewModel
        SelectionTarget.Destination -> destinationSelectorState to destinationSelectorViewModel
        is SelectionTarget.EditWaypoint, SelectionTarget.NewWaypoint -> waypointSelectorState to waypointSelectorViewModel
        null -> originSelectorState to originSelectorViewModel
    }

    val onPlaceSelected: (Place) -> Unit = { place ->
        viewModel.onAction(CreateRouteAction.OnPlaceSelectedFromResult(place))
        activeSelectorViewModel.onAction(PlaceSelectorAction.OnDismiss)
    }
    // Row taps (saved/campus place) call onPlaceSelected directly, but "use current location"
    // and search-suggestion taps only emit PlaceSelectorEvent.PlaceSelected — without observing
    // it here, those two paths silently did nothing and leaked a suspended coroutine on every tap
    // (the emit has no collector to receive it).
    ObserveAsEvents(activeSelectorViewModel.events) { event ->
        when (event) {
            is PlaceSelectorEvent.PlaceSelected -> onPlaceSelected(event.place)
            PlaceSelectorEvent.NavigateToAddPlace -> onNavigateToAddPlace()
            PlaceSelectorEvent.Dismiss -> Unit
        }
    }

    CreateRouteScreenContent(
        state = state,
        placeSelectorState = activeSelectorState,
        onAction = viewModel::onAction,
        onPlaceSelectorAction = activeSelectorViewModel::onAction,
        onPlaceSelected = onPlaceSelected
    )
}

@Composable
fun CreateRouteScreenContent(
    state: CreateRouteUiState,
    placeSelectorState: PlaceSelectorUiState,
    onAction: (CreateRouteAction) -> Unit,
    onPlaceSelectorAction: (PlaceSelectorAction) -> Unit,
    onPlaceSelected: (Place) -> Unit,
) {
    if (state.selectionTarget != null) {
        PlaceSelectorContent(
            state = placeSelectorState,
            onAction = onPlaceSelectorAction,
            onPlaceSelected = onPlaceSelected,
            onBack = {
                onAction(CreateRouteAction.OnCancelSelection)
            },
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
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = state.typicalDepartureTime?.hour ?: 7,
        initialMinute = state.typicalDepartureTime?.minute ?: 0
    )

    if (showTimePicker) {
        TimePickerDialog(
            onCancel = { showTimePicker = false },
            onConfirm = {
                onAction(
                    CreateRouteAction.OnSetDepartureTime(
                        LocalTime(timePickerState.hour, timePickerState.minute)
                    )
                )
                showTimePicker = false
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }

    Scaffold(
        topBar = {
            CarpoolBackTopBar(
                title = stringResource(Res.string.create_route_title),
                onBack = { onAction(CreateRouteAction.OnBackClick) },
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding(),
            contentPadding = PaddingValues(bottom = Spacing.lg)
        ) {
            // Route name field
            item {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { onAction(CreateRouteAction.OnNameChange(it)) },
                    label = { Text(stringResource(Res.string.route_name_label)) },
                    placeholder = { Text(stringResource(Res.string.route_name_placeholder)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm),
                    singleLine = true,
                    isError = state.error is CreateRouteError.NameRequired
                )
            }

            // Trajectory section
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

            itemsIndexed(
                state.waypoints,
                key = { index, waypoint -> waypoint.id.ifBlank { "waypoint_$index" } }
            ) { index, waypoint ->
                RouteStopItem(
                    label = stringResource(Res.string.stop_number, index + 1),
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
                    // 40dp aligns the label under RouteStopItem's content column (24dp timeline +
                    // 16dp spacer), not a spacing-scale value.
                    modifier = Modifier.padding(horizontal = 40.dp)
                ) {
                    Icon(vectorResource(Res.drawable.add_24px), contentDescription = null)
                    Spacer(Modifier.width(Spacing.sm))
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

            // Recurrence section
            item {
                SectionHeader(stringResource(Res.string.recurrence_section_title))
            }

            item {
                DaySelector(
                    selectedDays = state.recurringDays,
                    onToggleDay = { onAction(CreateRouteAction.OnToggleRecurringDay(it)) },
                    modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
                )
            }

            item {
                val timeLabel = state.typicalDepartureTime?.let { t ->
                    stringResource(
                        Res.string.departure_time_label,
                        "${t.hour.toString().padStart(2, '0')}:${t.minute.toString().padStart(2, '0')}"
                    )
                } ?: stringResource(Res.string.departure_time_not_set)
                TextButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.padding(horizontal = Spacing.sm)
                ) {
                    Text(timeLabel)
                }
            }

            // Error
            item {
                if (state.error != null) {
                    Text(
                        text = stringResource(state.error.asStringResource()),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(Spacing.lg)
                    )
                }
            }

            // Save button
            item {
                Button(
                    onClick = { onAction(CreateRouteAction.OnSaveClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg),
                    enabled = state.isValid && !state.isLoading
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
                name = "Ida a clase",
                origin = Place(
                    name = "Casa",
                    address = "Calle 10 #20-30",
                    latitude = 0.0,
                    longitude = 0.0
                ),
                waypoints = listOf(
                    Place(name = "Parada 1", address = "Cra 50 #30", latitude = 0.0, longitude = 0.0)
                )
            ),
            onAction = {}
        )
    }
}
