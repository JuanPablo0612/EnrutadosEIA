package com.juanpablo0612.carpool.presentation.routes.create

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.routes.model.RouteType
import com.juanpablo0612.carpool.presentation.places.selector.PlaceSelectorContent
import com.juanpablo0612.carpool.presentation.places.selector.PlaceSelectorViewModel
import com.juanpablo0612.carpool.presentation.routes.create.components.DaySelector
import com.juanpablo0612.carpool.presentation.routes.create.components.RouteStopItem
import com.juanpablo0612.carpool.presentation.routes.create.components.RouteTypeToggle
import com.juanpablo0612.carpool.presentation.routes.create.components.SectionHeader
import com.juanpablo0612.carpool.presentation.routes.create.components.StopType
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.components.TimePickerDialog
import enrutadoseia.composeapp.generated.resources.*
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateRouteScreen(
    viewModel: CreateRouteViewModel,
    onBackClick: () -> Unit,
    onRouteCreated: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val placeSelectorViewModel: PlaceSelectorViewModel = koinViewModel()
    val placeSelectorState = placeSelectorViewModel.state.collectAsState().value

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CreateRouteEvent.NavigateBack -> onBackClick()
            is CreateRouteEvent.RouteCreated -> onRouteCreated()
            is CreateRouteEvent.ShowError -> {
                // Using global ErrorMessage component or handling state error
            }
        }
    }

    if (state.selectionTarget != null) {
        PlaceSelectorContent(
            state = placeSelectorState,
            onAction = placeSelectorViewModel::onAction,
            onPlaceSelected = { place ->
                viewModel.onAction(CreateRouteAction.OnPlaceSelectedFromResult(place))
            },
            onBack = {
                viewModel.onAction(CreateRouteAction.OnCancelSelection)
            }
        )
    } else {
        CreateRouteContent(
            state = state,
            onAction = viewModel::onAction
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
                RouteTypeToggle(
                    selectedType = state.routeType,
                    onTypeChange = { onAction(CreateRouteAction.OnRouteTypeChange(it)) }
                )
            }

            item {
                SectionHeader(stringResource(Res.string.waypoints_section_title))
            }

            item {
                RouteStopItem(
                    label = if (state.routeType is RouteType.ToUniversity) 
                        stringResource(Res.string.origin_label) 
                    else 
                        stringResource(Res.string.origin_eia_label),
                    place = state.origin,
                    type = StopType.START,
                    isLocked = state.routeType is RouteType.FromUniversity,
                    onClick = { onAction(CreateRouteAction.OnWaypointClick(-1)) }
                )
            }

            itemsIndexed(state.waypoints) { index, waypoint ->
                RouteStopItem(
                    label = "Stop ${index + 1}",
                    place = waypoint,
                    type = StopType.MIDDLE,
                    isLocked = false,
                    onRemove = { onAction(CreateRouteAction.OnRemoveWaypoint(index)) },
                    onClick = { onAction(CreateRouteAction.OnWaypointClick(index)) }
                )
            }

            item {
                TextButton(
                    onClick = { onAction(CreateRouteAction.OnWaypointClick(state.waypoints.size)) },
                    modifier = Modifier.padding(horizontal = 40.dp) // Align with text
                ) {
                    Icon(vectorResource(Res.drawable.add_24px), contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(Res.string.add_waypoint_button))
                }
            }

            item {
                RouteStopItem(
                    label = if (state.routeType is RouteType.FromUniversity) 
                        stringResource(Res.string.destination_label) 
                    else 
                        stringResource(Res.string.destination_eia_label),
                    place = state.destination,
                    type = StopType.END,
                    isLocked = state.routeType is RouteType.ToUniversity,
                    showConnector = false,
                    onClick = { onAction(CreateRouteAction.OnWaypointClick(-2)) }
                )
            }

            item {
                HorizontalDivider(Modifier.padding(vertical = 16.dp, horizontal = 16.dp))
            }

            item {
                val timeLabel = if (state.routeType is RouteType.ToUniversity) 
                    stringResource(Res.string.arrival_time_label) 
                else 
                    stringResource(Res.string.departure_time_label)
                
                Column(Modifier.padding(horizontal = 16.dp).clickable { showTimePicker = true }) {
                    Text(timeLabel, style = MaterialTheme.typography.titleMedium)
                    Text(
                        state.targetTime.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            item {
                DaySelector(
                    selectedDays = state.selectedDays,
                    onDayToggled = { onAction(CreateRouteAction.OnDayToggled(it)) },
                    modifier = Modifier.padding(top = 16.dp)
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

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = state.targetTime.hour,
            initialMinute = state.targetTime.minute
        )
        TimePickerDialog(
            onCancel = { showTimePicker = false },
            onConfirm = {
                onAction(CreateRouteAction.OnTimeChange(LocalTime(timePickerState.hour, timePickerState.minute)))
                showTimePicker = false
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}
