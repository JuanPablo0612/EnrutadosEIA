package com.juanpablo0612.carpool.presentation.route.create

import com.juanpablo0612.carpool.presentation.place.stops.StopSelectionHost
import com.juanpablo0612.carpool.presentation.place.stops.StopsDraft
import com.juanpablo0612.carpool.presentation.place.stops.stopsEditorItems
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.presentation.route.create.components.DaySelector
import com.juanpablo0612.carpool.presentation.route.create.components.SectionHeader
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.FormProgressIndicator
import com.juanpablo0612.carpool.presentation.ui.components.SuccessMessage
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.components.TimePickerDialog
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.*
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateRouteScreen(
    viewModel: CreateRouteViewModel,
    onBackClick: () -> Unit,
    onRouteCreated: () -> Unit,
    onNavigateToAddPlace: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            CreateRouteEvent.NavigateBack -> onBackClick()
            CreateRouteEvent.RouteCreated -> onRouteCreated()
        }
    }

    // Route system back through the same dirty-check as the top-bar back arrow, so a swipe/
    // gesture back can't silently discard a partially-filled draft either.
    BackHandler(enabled = state.selectionTarget == null) {
        viewModel.onAction(CreateRouteAction.OnBackClick)
    }

    StopSelectionHost(
        selectionTarget = state.selectionTarget,
        onPlaceSelected = { viewModel.onAction(CreateRouteAction.OnPlaceSelectedFromResult(it)) },
        onCancelSelection = { viewModel.onAction(CreateRouteAction.OnCancelSelection) },
        onNavigateToAddPlace = onNavigateToAddPlace,
    ) {
        CreateRouteContent(state = state, onAction = viewModel::onAction)
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

    if (state.showDiscardConfirm) {
        ConfirmDialog(
            title = stringResource(Res.string.discard_changes_title),
            description = stringResource(Res.string.discard_changes_body),
            confirmText = stringResource(Res.string.discard_changes_confirm),
            onConfirm = { onAction(CreateRouteAction.OnConfirmDiscard) },
            onDismiss = { onAction(CreateRouteAction.OnDismissDiscardConfirm) },
            isDestructive = true
        )
    }

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
            item {
                val completed = listOf(
                    state.name.isNotBlank(),
                    state.stops.origin != null,
                    state.stops.destination != null
                ).count { it }
                FormProgressIndicator(
                    completedSections = completed,
                    totalSections = 3,
                    label = stringResource(Res.string.form_progress_label, completed, 3),
                    modifier = Modifier.padding(vertical = Spacing.sm)
                )
            }

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
                    isError = state.error is CreateRouteError.NameRequired,
                    supportingText = if (state.error is CreateRouteError.NameRequired) {
                        { Text(stringResource(state.error.asStringResource())) }
                    } else null
                )
            }

            // Trajectory section
            item {
                SectionHeader(stringResource(Res.string.waypoints_section_title))
            }

            stopsEditorItems(
                stops = state.stops,
                onOriginClick = { onAction(CreateRouteAction.OnOriginClick) },
                onDestinationClick = { onAction(CreateRouteAction.OnDestinationClick) },
                onEditWaypoint = { onAction(CreateRouteAction.OnEditWaypointClick(it)) },
                onRemoveWaypoint = { onAction(CreateRouteAction.OnRemoveWaypoint(it)) },
                onAddWaypoint = { onAction(CreateRouteAction.OnAddWaypointClick) },
            )

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

            // General error — field-specific errors (e.g. NameRequired) are shown inline on
            // their own field instead, via supportingText.
            item {
                if (state.error != null && state.error !is CreateRouteError.NameRequired) {
                    ErrorMessage(
                        message = stringResource(state.error.asStringResource()),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm)
                    )
                }
            }

            if (state.isSaved) {
                item {
                    SuccessMessage(
                        message = stringResource(Res.string.notice_route_created),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm)
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
                    enabled = state.isValid && !state.isLoading && !state.isSaved
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
                stops = StopsDraft(
                    origin = Place(name = "Casa", address = "Calle 10 #20-30", latitude = 0.0, longitude = 0.0),
                    waypoints = listOf(
                        Place(name = "Parada 1", address = "Cra 50 #30", latitude = 0.0, longitude = 0.0)
                    ),
                ),
            ),
            onAction = {}
        )
    }
}
