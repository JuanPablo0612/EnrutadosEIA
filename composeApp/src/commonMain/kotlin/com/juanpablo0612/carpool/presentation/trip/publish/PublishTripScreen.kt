package com.juanpablo0612.carpool.presentation.trip.publish

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.domain.trip.validation.TripDraftValidator
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.presentation.place.stops.StopSelectionHost
import com.juanpablo0612.carpool.presentation.place.stops.StopsDraft
import com.juanpablo0612.carpool.presentation.place.stops.stopsEditorItems
import com.juanpablo0612.carpool.presentation.trip.TripError
import com.juanpablo0612.carpool.presentation.trip.asStringResource
import com.juanpablo0612.carpool.presentation.trip.publish.components.DateChip
import com.juanpablo0612.carpool.presentation.trip.publish.components.PublishSummary
import com.juanpablo0612.carpool.presentation.trip.publish.components.PublishSummarySheet
import com.juanpablo0612.carpool.presentation.trip.publish.components.SectionLabel
import com.juanpablo0612.carpool.presentation.trip.publish.components.SingleVehicleCard
import com.juanpablo0612.carpool.presentation.trip.publish.components.TripContributionSection
import com.juanpablo0612.carpool.presentation.trip.publish.components.TripMessageSection
import com.juanpablo0612.carpool.presentation.trip.publish.components.TripSeatsSection
import com.juanpablo0612.carpool.presentation.trip.publish.components.TripWhenSection
import com.juanpablo0612.carpool.presentation.trip.publish.components.VehicleRadioItem
import com.juanpablo0612.carpool.presentation.ui.components.ActionButton
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTextField
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.DaySelector
import com.juanpablo0612.carpool.presentation.ui.components.DetailSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.components.SectionHeader
import com.juanpablo0612.carpool.presentation.ui.components.TimePickerDialog
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.formatLongDate
import com.juanpablo0612.carpool.presentation.ui.util.formatPesos
import com.juanpablo0612.carpool.presentation.ui.util.formatShortTime
import com.juanpablo0612.carpool.presentation.ui.util.rememberNotificationPermissionState
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.cancel
import enrutadoseia.composeapp.generated.resources.cd_reverse_stops
import enrutadoseia.composeapp.generated.resources.confirm
import enrutadoseia.composeapp.generated.resources.create_trip_title
import enrutadoseia.composeapp.generated.resources.date_of_connector
import enrutadoseia.composeapp.generated.resources.day_names_short
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.discard_changes_body
import enrutadoseia.composeapp.generated.resources.discard_changes_confirm
import enrutadoseia.composeapp.generated.resources.discard_changes_title
import enrutadoseia.composeapp.generated.resources.month_names
import enrutadoseia.composeapp.generated.resources.publish_trip_linked_route_hint
import enrutadoseia.composeapp.generated.resources.publish_trip_review_button
import enrutadoseia.composeapp.generated.resources.publish_trip_route_section
import enrutadoseia.composeapp.generated.resources.publish_trip_save_as_route
import enrutadoseia.composeapp.generated.resources.publish_trip_save_as_route_description
import enrutadoseia.composeapp.generated.resources.publish_trip_saved_routes_label
import enrutadoseia.composeapp.generated.resources.recurrence_section_title
import enrutadoseia.composeapp.generated.resources.route_from_to
import enrutadoseia.composeapp.generated.resources.route_name_label
import enrutadoseia.composeapp.generated.resources.route_name_placeholder
import enrutadoseia.composeapp.generated.resources.select_vehicle_section
import enrutadoseia.composeapp.generated.resources.swap_horiz_24px
import enrutadoseia.composeapp.generated.resources.time_am
import enrutadoseia.composeapp.generated.resources.time_pm
import enrutadoseia.composeapp.generated.resources.trip_bottom_summary
import enrutadoseia.composeapp.generated.resources.trip_bottom_summary_with_contribution
import enrutadoseia.composeapp.generated.resources.trip_no_vehicle_title
import enrutadoseia.composeapp.generated.resources.trip_register_another_vehicle
import enrutadoseia.composeapp.generated.resources.trip_register_vehicle_action
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PublishTripScreen(
    viewModel: PublishTripViewModel,
    onBackClick: () -> Unit,
    onTripPublished: () -> Unit,
    onNavigateToRegisterVehicle: () -> Unit,
    onNavigateToAddPlace: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val notificationPermission = rememberNotificationPermissionState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            PublishTripEvent.TripPublished -> {
                // Passengers' seat requests are what a new driver needs to hear about.
                notificationPermission.request()
                onTripPublished()
            }
            PublishTripEvent.NavigateBack -> onBackClick()
            PublishTripEvent.NavigateToRegisterVehicle -> onNavigateToRegisterVehicle()
        }
    }

    // Route system back through the same dirty-check as the top-bar arrow.
    BackHandler(enabled = state.selectionTarget == null) {
        viewModel.onAction(PublishTripAction.OnBackClick)
    }

    StopSelectionHost(
        selectionTarget = state.selectionTarget,
        onPlaceSelected = { viewModel.onAction(PublishTripAction.OnPlaceSelected(it)) },
        onCancelSelection = { viewModel.onAction(PublishTripAction.OnCancelSelection) },
        onNavigateToAddPlace = onNavigateToAddPlace,
    ) {
        PublishTripContent(state = state, onAction = viewModel::onAction)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishTripContent(
    state: PublishTripUiState,
    onAction: (PublishTripAction) -> Unit,
) {
    val timeZone = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(timeZone).date
    val tomorrow = today.plus(1, DateTimeUnit.DAY)
    val lastBookableDay = today.plus(TripDraftValidator.MAX_DAYS_AHEAD, DateTimeUnit.DAY)

    val dayNames = stringArrayResource(Res.array.day_names_short).toList()
    val monthNames = stringArrayResource(Res.array.month_names).toList()
    val dateConnector = stringResource(Res.string.date_of_connector)
    val amMarker = stringResource(Res.string.time_am)
    val pmMarker = stringResource(Res.string.time_pm)
    val formattedDate = state.departureDate?.let {
        formatLongDate(it.year, it.month.number, it.day, dayNames, monthNames, dateConnector)
    }.orEmpty()
    val formattedTime = state.departureTime?.let { formatShortTime(it.hour, it.minute, amMarker, pmMarker) }.orEmpty()

    if (state.showDiscardConfirm) {
        ConfirmDialog(
            title = stringResource(Res.string.discard_changes_title),
            description = stringResource(Res.string.discard_changes_body),
            confirmText = stringResource(Res.string.discard_changes_confirm),
            onConfirm = { onAction(PublishTripAction.OnConfirmDiscard) },
            onDismiss = { onAction(PublishTripAction.OnDismissDiscard) },
            isDestructive = true,
        )
    }

    if (state.showDatePicker) {
        // The Material date picker works in UTC-midnight millis, independent of the device zone.
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = (state.departureDate ?: today).toUtcMillis(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    utcTimeMillis.toUtcDate() in today..lastBookableDay
            },
        )
        DatePickerDialog(
            onDismissRequest = { onAction(PublishTripAction.OnDismissDatePicker) },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onAction(PublishTripAction.OnDateSelected(it.toUtcDate())) }
                }) { Text(stringResource(Res.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { onAction(PublishTripAction.OnDismissDatePicker) }) {
                    Text(stringResource(Res.string.cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (state.showTimePicker) {
        val initial = state.departureTime ?: LocalTime(7, 0)
        val timePickerState = rememberTimePickerState(initialHour = initial.hour, initialMinute = initial.minute, is24Hour = false)
        TimePickerDialog(
            onCancel = { onAction(PublishTripAction.OnDismissTimePicker) },
            onConfirm = { onAction(PublishTripAction.OnTimeSelected(LocalTime(timePickerState.hour, timePickerState.minute))) },
        ) {
            TimePicker(state = timePickerState)
        }
    }

    if (state.showSummary) {
        PublishSummarySheet(
            summary = PublishSummary(
                stopNames = listOfNotNull(state.stops.origin?.name) +
                    state.stops.waypoints.map { it.name } +
                    listOfNotNull(state.stops.destination?.name),
                dateText = formattedDate,
                timeText = formattedTime,
                seatCount = state.seatCount,
                contributionPerPassenger = state.contributionPerPassenger,
                vehicleText = state.selectedVehicle?.let {
                    listOf("${it.brand} ${it.model}".trim(), it.licensePlate).filter(String::isNotBlank).joinToString(" · ")
                },
                message = state.message,
                routeNameToSave = state.routeName.trim().takeIf { state.isFromScratch && state.saveAsRoute },
            ),
            isPublishing = state.isPublishing,
            onConfirm = { onAction(PublishTripAction.OnConfirmPublish) },
            onDismiss = { onAction(PublishTripAction.OnDismissSummary) },
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CarpoolBackTopBar(
                title = stringResource(Res.string.create_trip_title),
                onBack = { onAction(PublishTripAction.OnBackClick) },
            )
        },
        bottomBar = {
            if (!state.isLoading) {
                Surface(color = MaterialTheme.colorScheme.surfaceContainerLowest, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Column(
                            modifier = Modifier
                                .navigationBarsPadding()
                                .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.md),
                            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                        ) {
                            Text(
                                text = bottomSummary(state, formattedDate, formattedTime),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            // Stays enabled: tapping it with problems shows them next to each field.
                            PrimaryButton(
                                text = stringResource(Res.string.publish_trip_review_button),
                                onClick = { onAction(PublishTripAction.OnReviewClick) },
                                isLoading = state.isPublishing,
                            )
                        }
                    }
                }
            }
        },
    ) { padding ->
        if (state.isLoading) {
            DetailSkeleton(modifier = Modifier.fillMaxSize().padding(padding))
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding).imePadding(),
            contentPadding = PaddingValues(bottom = Spacing.lg),
        ) {
            if (state.savedRoutes.isNotEmpty()) {
                item(key = "saved_routes") {
                    SavedRouteChips(
                        routes = state.savedRoutes,
                        selectedRouteId = state.linkedRoute?.id,
                        onRouteClick = { onAction(PublishTripAction.OnSavedRouteClick(it)) },
                    )
                }
            }

            item(key = "route_header") {
                SectionHeader(
                    title = stringResource(Res.string.publish_trip_route_section),
                    modifier = Modifier.padding(top = Spacing.md),
                    action = {
                        IconButton(onClick = { onAction(PublishTripAction.OnReverseStops) }, enabled = state.stops.isComplete) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.swap_horiz_24px),
                                contentDescription = stringResource(Res.string.cd_reverse_stops),
                                modifier = Modifier.rotate(90f),
                            )
                        }
                    },
                )
            }
            stopsEditorItems(
                stops = state.stops,
                onOriginClick = { onAction(PublishTripAction.OnOriginClick) },
                onDestinationClick = { onAction(PublishTripAction.OnDestinationClick) },
                onEditWaypoint = { onAction(PublishTripAction.OnEditWaypointClick(it)) },
                onRemoveWaypoint = { onAction(PublishTripAction.OnRemoveWaypoint(it)) },
                onAddWaypoint = { onAction(PublishTripAction.OnAddWaypointClick) },
            )
            if (state.linkedRoute != null) {
                item(key = "linked_hint") {
                    Text(
                        text = stringResource(Res.string.publish_trip_linked_route_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = Spacing.screenHorizontal),
                    )
                }
            }
            fieldErrorItem("stops_error", state.fieldErrors, TripError.OriginDestinationRequired, TripError.SameOriginDestination)

            item(key = "when") {
                TripWhenSection(
                    dateChipState = when (state.departureDate) {
                        today -> DateChip.Today
                        tomorrow -> DateChip.Tomorrow
                        else -> DateChip.Other
                    },
                    formattedDate = formattedDate,
                    formattedTime = formattedTime,
                    onSelectToday = { onAction(PublishTripAction.OnSelectToday) },
                    onSelectTomorrow = { onAction(PublishTripAction.OnSelectTomorrow) },
                    onShowDatePicker = { onAction(PublishTripAction.OnShowDatePicker) },
                    onShowTimePicker = { onAction(PublishTripAction.OnShowTimePicker) },
                    modifier = Modifier.sectionPadding(),
                )
            }
            fieldErrorItem("when_error", state.fieldErrors, TripError.DepartureTooSoon, TripError.DepartureTooFar)

            vehicleItems(state, onAction)

            if (state.vehicles.isNotEmpty()) {
                item(key = "seats") {
                    TripSeatsSection(
                        seatCount = state.seatCount,
                        selectedVehicle = state.selectedVehicle,
                        onChange = { onAction(PublishTripAction.OnSetSeats(it)) },
                        modifier = Modifier.sectionPadding(),
                    )
                }
                fieldErrorItem("seats_error", state.fieldErrors, TripError.SeatsOutOfRange)
                item(key = "contribution") {
                    TripContributionSection(
                        contributionPerPassenger = state.contributionPerPassenger,
                        onContributionChange = { onAction(PublishTripAction.OnSetContribution(it)) },
                        modifier = Modifier.sectionPadding(),
                    )
                }
                fieldErrorItem("contribution_error", state.fieldErrors, TripError.ContributionOutOfRange)
                item(key = "message") {
                    TripMessageSection(
                        message = state.message,
                        onMessageChange = { onAction(PublishTripAction.OnSetMessage(it)) },
                        modifier = Modifier.sectionPadding(),
                    )
                }
                fieldErrorItem("message_error", state.fieldErrors, TripError.MessageTooLong)
            }

            if (state.isFromScratch) {
                item(key = "save_as_route") { SaveAsRouteSection(state, onAction) }
            }

            state.error?.let { error ->
                item(key = "error") {
                    ErrorMessage(
                        message = stringResource(error.asStringResource()),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm)
                            .semantics { liveRegion = LiveRegionMode.Polite },
                    )
                }
            }
        }
    }
}

@Composable
private fun bottomSummary(state: PublishTripUiState, formattedDate: String, formattedTime: String): String {
    val contribution = state.contributionPerPassenger?.let(::formatPesos)
    return if (contribution != null) {
        stringResource(Res.string.trip_bottom_summary_with_contribution, formattedDate, formattedTime, state.seatCount, contribution)
    } else {
        stringResource(Res.string.trip_bottom_summary, formattedDate, formattedTime, state.seatCount)
    }
}

@Composable
private fun SavedRouteChips(routes: List<Route>, selectedRouteId: String?, onRouteClick: (String) -> Unit) {
    Column {
        SectionLabel(
            text = stringResource(Res.string.publish_trip_saved_routes_label),
            modifier = Modifier.padding(start = Spacing.screenHorizontal, end = Spacing.screenHorizontal, top = Spacing.lg, bottom = Spacing.sm),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = Spacing.screenHorizontal),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            items(routes, key = { it.id }) { route ->
                FilterChip(
                    selected = route.id == selectedRouteId,
                    onClick = { onRouteClick(route.id) },
                    label = {
                        Text(route.name.ifBlank { stringResource(Res.string.route_from_to, route.origin.name, route.destination.name) })
                    },
                )
            }
        }
    }
}

private fun LazyListScope.vehicleItems(state: PublishTripUiState, onAction: (PublishTripAction) -> Unit) {
    item(key = "vehicle_label") {
        SectionLabel(
            text = stringResource(Res.string.select_vehicle_section),
            modifier = Modifier
                .sectionPadding()
                .padding(bottom = Spacing.sm),
        )
    }
    when {
        state.vehicles.isEmpty() -> item(key = "vehicle_empty") {
            EmptyState(
                icon = vectorResource(Res.drawable.directions_car_24px),
                title = stringResource(Res.string.trip_no_vehicle_title),
                description = "",
                primaryAction = ActionButton(
                    label = stringResource(Res.string.trip_register_vehicle_action),
                    onClick = { onAction(PublishTripAction.OnRegisterVehicleClick) },
                ),
                modifier = Modifier.padding(Spacing.lg),
            )
        }
        state.vehicles.size == 1 -> item(key = "vehicle_single") {
            SingleVehicleCard(
                vehicle = state.vehicles.first(),
                onChangeClick = null,
                modifier = Modifier.padding(horizontal = Spacing.screenHorizontal),
            )
        }
        else -> item(key = "vehicle_list") {
            LazyRow(
                contentPadding = PaddingValues(horizontal = Spacing.screenHorizontal),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                items(state.vehicles, key = { it.id }) { vehicle ->
                    VehicleRadioItem(
                        vehicle = vehicle,
                        isSelected = vehicle.id == state.selectedVehicleId,
                        onClick = { onAction(PublishTripAction.OnVehicleSelected(vehicle.id)) },
                    )
                }
            }
        }
    }
    if (state.vehicles.isNotEmpty()) {
        item(key = "vehicle_register_another") {
            TextButton(
                onClick = { onAction(PublishTripAction.OnRegisterVehicleClick) },
                modifier = Modifier.padding(horizontal = Spacing.sm),
            ) { Text(stringResource(Res.string.trip_register_another_vehicle)) }
        }
    }
    fieldErrorItem("vehicle_error", state.fieldErrors, TripError.NoVehicleSelected)
}

@Composable
private fun SaveAsRouteSection(state: PublishTripUiState, onAction: (PublishTripAction) -> Unit) {
    CarpoolListCard(modifier = Modifier.sectionPadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    value = state.saveAsRoute,
                    role = Role.Switch,
                    onValueChange = { onAction(PublishTripAction.OnToggleSaveAsRoute(it)) },
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(Res.string.publish_trip_save_as_route), style = MaterialTheme.typography.titleMedium)
                Text(
                    stringResource(Res.string.publish_trip_save_as_route_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            // onCheckedChange = null: the whole row toggles, so the switch is not a second target.
            Switch(checked = state.saveAsRoute, onCheckedChange = null)
        }
        AnimatedVisibility(visible = state.saveAsRoute) {
            Column(
                modifier = Modifier.padding(top = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                val nameError = TripError.RouteNameRequired in state.fieldErrors
                CarpoolTextField(
                    value = state.routeName,
                    onValueChange = { onAction(PublishTripAction.OnRouteNameChange(it)) },
                    label = stringResource(Res.string.route_name_label),
                    placeholder = stringResource(Res.string.route_name_placeholder),
                    errorMessage = if (nameError) stringResource(TripError.RouteNameRequired.asStringResource()) else null,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                )
                Text(stringResource(Res.string.recurrence_section_title), style = MaterialTheme.typography.titleSmall)
                DaySelector(
                    selectedDays = state.recurringDays,
                    onToggleDay = { onAction(PublishTripAction.OnToggleRecurringDay(it)) },
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                )
            }
        }
    }
}

/** Each field group sits in the screen gutter with room above it to separate it from the last. */
private fun Modifier.sectionPadding(): Modifier =
    padding(start = Spacing.screenHorizontal, end = Spacing.screenHorizontal, top = Spacing.xl)

/** An inline error under a section, for whichever of [relevant] is in [errors]. */
private fun LazyListScope.fieldErrorItem(key: String, errors: Set<TripError>, vararg relevant: TripError) {
    val error = relevant.firstOrNull { it in errors } ?: return
    item(key = key) {
        Text(
            text = stringResource(error.asStringResource()),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.xs)
                .semantics { liveRegion = LiveRegionMode.Polite },
        )
    }
}

private fun LocalDate.toUtcMillis(): Long = LocalDateTime(this, LocalTime(0, 0)).toInstant(TimeZone.UTC).toEpochMilliseconds()

private fun Long.toUtcDate(): LocalDate = Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date

@Preview
@Composable
private fun PublishTripFromScratchPreview() {
    CarpoolTheme {
        PublishTripContent(
            state = PublishTripUiState(
                isLoading = false,
                stops = StopsDraft(
                    origin = Place(name = "Casa", address = "Calle 10 #20-30", latitude = 6.2, longitude = -75.6),
                    destination = Place.EIA_LAS_PALMAS,
                ),
                departureDate = LocalDate(2026, 10, 2),
                departureTime = LocalTime(6, 45),
                vehicles = listOf(
                    Vehicle(
                        id = "v1", driverId = "d1", brand = "Mazda", model = "3",
                        licensePlate = "ABC123", color = "Gris", year = 2020, seatsAvailable = 4,
                    )
                ),
                selectedVehicleId = "v1",
                seatCount = 3,
                contributionPerPassenger = 5_000,
                saveAsRoute = true,
                routeName = "Ida a la U",
                fieldErrors = setOf(TripError.DepartureTooSoon),
            ),
            onAction = {},
        )
    }
}
