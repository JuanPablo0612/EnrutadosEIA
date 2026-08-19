package com.juanpablo0612.carpool.presentation.route.detail

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorContent
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorViewModel
import com.juanpablo0612.carpool.presentation.route.create.CreateRouteUiState
import com.juanpablo0612.carpool.presentation.route.create.components.DaySelector
import com.juanpablo0612.carpool.presentation.route.create.components.RouteStopItem
import com.juanpablo0612.carpool.presentation.route.create.components.SectionHeader
import com.juanpablo0612.carpool.presentation.route.create.components.StopType
import com.juanpablo0612.carpool.presentation.route.detail.components.RecurrenceRow
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.DetailSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.components.TimePickerDialog
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.*
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun RouteDetailScreen(
    viewModel: RouteDetailViewModel,
    onBackClick: () -> Unit,
    onNavigateToAddPlace: () -> Unit,
    onNavigateToCreateTrip: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val placeSelectorViewModel: PlaceSelectorViewModel = koinViewModel { parametersOf("ORIGIN") }
    val placeSelectorState by placeSelectorViewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RouteDetailEvent.RouteUpdated -> onBackClick()
            RouteDetailEvent.NavigateBack -> onBackClick()
            is RouteDetailEvent.NavigateToCreateTrip -> onNavigateToCreateTrip(event.routeId)
        }
    }

    if (state.showDeleteConfirm) {
        ConfirmDialog(
            title = stringResource(Res.string.route_delete_confirm_title),
            description = stringResource(Res.string.route_delete_confirm_description),
            confirmText = stringResource(Res.string.route_delete_confirm_button),
            onConfirm = { viewModel.onAction(RouteDetailAction.OnConfirmDelete) },
            onDismiss = { viewModel.onAction(RouteDetailAction.OnDismissDelete) },
            isDestructive = true
        )
    }

    val draft = state.draft
    when {
        draft?.selectionTarget != null -> {
            PlaceSelectorContent(
                state = placeSelectorState,
                onAction = placeSelectorViewModel::onAction,
                onPlaceSelected = { place ->
                    viewModel.onAction(RouteDetailAction.OnPlaceSelectedFromResult(place))
                },
                onBack = { viewModel.onAction(RouteDetailAction.OnCancelSelection) },
                onNavigateToAddPlace = onNavigateToAddPlace
            )
        }
        state.isEditing && draft != null -> {
            RouteDetailEditContent(
                draft = draft,
                isSaving = state.isSaving,
                onAction = viewModel::onAction
            )
        }
        else -> {
            RouteDetailReadContent(
                state = state,
                onAction = viewModel::onAction
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RouteDetailReadContent(
    state: RouteDetailUiState,
    onAction: (RouteDetailAction) -> Unit
) {
    Scaffold(
        topBar = {
            CarpoolBackTopBar(
                title = state.route?.name?.takeIf { it.isNotBlank() }
                    ?: stringResource(Res.string.route_detail_title),
                onBack = { onAction(RouteDetailAction.OnBackClick) },
                actions = {
                    if (state.route != null) {
                        IconButton(onClick = { onAction(RouteDetailAction.OnEditClick) }) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.edit_24px),
                                contentDescription = stringResource(Res.string.cd_edit_route)
                            )
                        }
                        IconButton(onClick = { onAction(RouteDetailAction.OnDeleteClick) }) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.delete_24px),
                                contentDescription = stringResource(Res.string.cd_delete_route),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (state.route != null) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = Spacing.screenHorizontal)
                        .padding(bottom = Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Button(
                        onClick = { onAction(RouteDetailAction.OnPublishTripClick) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(Res.string.route_publish_trip_button))
                    }
                    TextButton(
                        onClick = { onAction(RouteDetailAction.OnDuplicateClick) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(Res.string.route_duplicate_button))
                    }
                }
            }
        }
    ) { padding ->
        if (state.isLoading) {
            DetailSkeleton(modifier = Modifier.fillMaxSize().padding(padding))
            return@Scaffold
        }

        val route = state.route ?: return@Scaffold

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = Spacing.lg)
        ) {
            // Map placeholder
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp) // component-intrinsic preview height, not a spacing value
                        .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.location_on_24px),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp), // icon-intrinsic size
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(Spacing.xs))
                            Text(
                                text = stringResource(Res.string.route_map_placeholder),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Recurrence row
            if (route.recurringDays.isNotEmpty()) {
                item {
                    RecurrenceRow(
                        recurringDays = route.recurringDays,
                        typicalDepartureTime = route.typicalDepartureTime
                    )
                }
            }

            // Trajectory section
            item { SectionHeader(stringResource(Res.string.route_detail_trajectory_section)) }

            item {
                RouteStopItem(
                    label = stringResource(Res.string.origin_label),
                    place = route.origin,
                    type = StopType.START,
                    isLocked = true,
                    onClick = {}
                )
            }

            itemsIndexed(route.waypoints) { index, waypoint ->
                RouteStopItem(
                    label = stringResource(Res.string.stop_number, index + 1),
                    place = waypoint,
                    type = StopType.MIDDLE,
                    isLocked = true,
                    onClick = {}
                )
            }

            item {
                RouteStopItem(
                    label = stringResource(Res.string.destination_label),
                    place = route.destination,
                    type = StopType.END,
                    isLocked = true,
                    showConnector = false,
                    onClick = {}
                )
            }

            // Stats section
            item { SectionHeader(stringResource(Res.string.route_detail_stats_section)) }

            item {
                Column(modifier = Modifier.padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.xs)) {
                    if (state.tripsPublished > 0) {
                        Text(
                            text = stringResource(Res.string.route_detail_trips_published, state.tripsPublished),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        state.lastUsedAt?.let { instant ->
                            val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                            Text(
                                text = stringResource(
                                    Res.string.route_detail_last_used,
                                    "${local.date.dayOfMonth}/${local.date.monthNumber}/${local.date.year}"
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(Res.string.route_detail_never_used),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            state.error?.let { error ->
                item {
                    Text(
                        text = stringResource(error.asStringResource()),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(Spacing.lg)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RouteDetailEditContent(
    draft: CreateRouteUiState,
    isSaving: Boolean,
    onAction: (RouteDetailAction) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = draft.typicalDepartureTime?.hour ?: 7,
        initialMinute = draft.typicalDepartureTime?.minute ?: 0
    )

    if (showTimePicker) {
        TimePickerDialog(
            onCancel = { showTimePicker = false },
            onConfirm = {
                onAction(
                    RouteDetailAction.OnSetDepartureTime(
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
                title = stringResource(Res.string.route_edit_title),
                onBack = { onAction(RouteDetailAction.OnCancelEdit) },
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = Spacing.lg)
        ) {
            // Name field
            item {
                OutlinedTextField(
                    value = draft.name,
                    onValueChange = { onAction(RouteDetailAction.OnNameChange(it)) },
                    label = { Text(stringResource(Res.string.route_name_label)) },
                    placeholder = { Text(stringResource(Res.string.route_name_placeholder)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm),
                    singleLine = true
                )
            }

            item { SectionHeader(stringResource(Res.string.waypoints_section_title)) }

            item {
                RouteStopItem(
                    label = stringResource(Res.string.origin_label),
                    place = draft.origin,
                    type = StopType.START,
                    isLocked = false,
                    onClick = { onAction(RouteDetailAction.OnOriginClick) }
                )
            }

            itemsIndexed(draft.waypoints) { index, waypoint ->
                RouteStopItem(
                    label = stringResource(Res.string.stop_number, index + 1),
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
                    place = draft.destination,
                    type = StopType.END,
                    isLocked = false,
                    showConnector = false,
                    onClick = { onAction(RouteDetailAction.OnDestinationClick) }
                )
            }

            item { SectionHeader(stringResource(Res.string.recurrence_section_title)) }

            item {
                DaySelector(
                    selectedDays = draft.recurringDays,
                    onToggleDay = { onAction(RouteDetailAction.OnToggleRecurringDay(it)) },
                    modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
                )
            }

            item {
                val timeLabel = draft.typicalDepartureTime?.let { t ->
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

            item {
                Button(
                    onClick = { onAction(RouteDetailAction.OnSaveChangesClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg),
                    enabled = draft.isValid && !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(Res.string.route_save_changes_button))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun RouteDetailReadPreview() {
    CarpoolTheme {
        RouteDetailReadContent(
            state = RouteDetailUiState(
                isLoading = false,
                route = Route(
                    id = "r1",
                    driverId = "d1",
                    name = "Ida a clase",
                    origin = Place(name = "Casa", address = "Calle 10 #20-30", latitude = 6.2, longitude = -75.6),
                    destination = Place(name = "EIA", address = "Cl. 49 Sur #50-90", latitude = 6.18, longitude = -75.59),
                    waypoints = listOf(
                        Place(name = "Parada 1", address = "Carrera 43A", latitude = 6.21, longitude = -75.57)
                    )
                ),
                tripsPublished = 5
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun RouteDetailEditPreview() {
    CarpoolTheme {
        RouteDetailEditContent(
            draft = CreateRouteUiState(
                name = "Ida a clase",
                origin = Place(name = "Casa", address = "Calle 10 #20-30", latitude = 6.2, longitude = -75.6),
                destination = Place(name = "EIA", address = "Cl. 49 Sur #50-90", latitude = 6.18, longitude = -75.59),
                waypoints = listOf(
                    Place(name = "Parada 1", address = "Carrera 43A", latitude = 6.21, longitude = -75.57)
                )
            ),
            isSaving = false,
            onAction = {}
        )
    }
}
