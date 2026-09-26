package com.juanpablo0612.carpool.presentation.route.detail

import com.juanpablo0612.carpool.presentation.place.stops.StopSelectionHost
import com.juanpablo0612.carpool.presentation.place.stops.StopsDraft
import com.juanpablo0612.carpool.presentation.place.stops.stopsEditorItems
import com.juanpablo0612.carpool.presentation.place.stops.stopsReadOnlyItems
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.place.model.Coordinates
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.presentation.place.add.components.MapRoutePreview
import com.juanpablo0612.carpool.presentation.route.create.CreateRouteUiState
import com.juanpablo0612.carpool.presentation.ui.components.DaySelector
import com.juanpablo0612.carpool.presentation.route.create.components.SectionHeader
import com.juanpablo0612.carpool.presentation.route.detail.components.RecurrenceRow
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.DetailSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.ErrorState
import com.juanpablo0612.carpool.presentation.ui.components.SuccessMessage
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.formatNumericDate
import com.juanpablo0612.carpool.presentation.ui.components.TimePickerDialog
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.*
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RouteDetailScreen(
    viewModel: RouteDetailViewModel,
    onBackClick: () -> Unit,
    onNavigateToAddPlace: () -> Unit,
    onNavigateToPublishTrip: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RouteDetailEvent.NavigateBack -> onBackClick()
            is RouteDetailEvent.NavigateToPublishTrip -> onNavigateToPublishTrip(event.routeId)
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
    val selectionTarget = draft?.selectionTarget

    // Route system back through the same dirty-check as the edit form's top-bar back arrow,
    // so a swipe/gesture back can't silently discard in-progress edits either.
    BackHandler(enabled = selectionTarget == null && state.isEditing) {
        viewModel.onAction(RouteDetailAction.OnCancelEdit)
    }

    StopSelectionHost(
        selectionTarget = selectionTarget,
        onPlaceSelected = { viewModel.onAction(RouteDetailAction.OnPlaceSelectedFromResult(it)) },
        onCancelSelection = { viewModel.onAction(RouteDetailAction.OnCancelSelection) },
        onNavigateToAddPlace = onNavigateToAddPlace,
    ) {
    when {
        state.isEditing && draft != null -> {
            RouteDetailEditContent(
                draft = draft,
                isSaving = state.isSaving,
                isSaved = state.isSaved,
                showDiscardConfirm = state.showDiscardEditConfirm,
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
                        enabled = !state.isDuplicating,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isDuplicating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text(stringResource(Res.string.route_duplicate_button))
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (state.isLoading) {
            DetailSkeleton(modifier = Modifier.fillMaxSize().padding(padding))
            return@Scaffold
        }

        if (state.route == null) {
            ErrorState(
                description = stringResource(
                    (state.error ?: RouteDetailError.NotFound).asStringResource()
                ),
                onRetry = { onAction(RouteDetailAction.OnRetry) },
                modifier = Modifier.fillMaxSize().padding(padding).padding(Spacing.lg)
            )
            return@Scaffold
        }

        val route = state.route

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = Spacing.lg)
        ) {
            // Route map: origin -> waypoints -> destination, in order.
            item {
                val stops = remember(route.id) {
                    (listOf(route.origin) + route.waypoints + route.destination)
                        .map { Coordinates(it.latitude, it.longitude) }
                }
                MapRoutePreview(
                    markers = stops,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp) // component-intrinsic preview height, not a spacing value
                        .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm)
                        .clip(MaterialTheme.shapes.medium),
                )
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

            stopsReadOnlyItems(StopsDraft.of(route))

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
                                    formatNumericDate(local.date)
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
                    ErrorMessage(
                        message = stringResource(error.asStringResource()),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm)
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
    isSaved: Boolean = false,
    showDiscardConfirm: Boolean = false,
    onAction: (RouteDetailAction) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = draft.typicalDepartureTime?.hour ?: 7,
        initialMinute = draft.typicalDepartureTime?.minute ?: 0
    )

    if (showDiscardConfirm) {
        ConfirmDialog(
            title = stringResource(Res.string.discard_changes_title),
            description = stringResource(Res.string.discard_changes_body),
            confirmText = stringResource(Res.string.discard_changes_confirm),
            onConfirm = { onAction(RouteDetailAction.OnConfirmDiscardEdit) },
            onDismiss = { onAction(RouteDetailAction.OnDismissDiscardEditConfirm) },
            isDestructive = true
        )
    }

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

            stopsEditorItems(
                stops = draft.stops,
                onOriginClick = { onAction(RouteDetailAction.OnOriginClick) },
                onDestinationClick = { onAction(RouteDetailAction.OnDestinationClick) },
                onEditWaypoint = { onAction(RouteDetailAction.OnEditWaypointClick(it)) },
                onRemoveWaypoint = { onAction(RouteDetailAction.OnRemoveWaypoint(it)) },
                onAddWaypoint = { onAction(RouteDetailAction.OnAddWaypointClick) },
            )

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

            if (isSaved) {
                item {
                    SuccessMessage(
                        message = stringResource(Res.string.notice_route_updated),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm)
                    )
                }
            }

            item {
                Button(
                    onClick = { onAction(RouteDetailAction.OnSaveChangesClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg),
                    enabled = draft.isValid && !isSaving && !isSaved
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
                stops = StopsDraft(
                    origin = Place(name = "Casa", address = "Calle 10 #20-30", latitude = 6.2, longitude = -75.6),
                    destination = Place(name = "EIA", address = "Cl. 49 Sur #50-90", latitude = 6.18, longitude = -75.59),
                    waypoints = listOf(
                        Place(name = "Parada 1", address = "Carrera 43A", latitude = 6.21, longitude = -75.57)
                    ),
                ),
            ),
            isSaving = false,
            onAction = {}
        )
    }
}
