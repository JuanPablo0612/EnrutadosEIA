package com.juanpablo0612.carpool.presentation.trip.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.Trip
import com.juanpablo0612.carpool.domain.trip.model.TripStatus
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.presentation.place.stops.StopSelectionHost
import com.juanpablo0612.carpool.presentation.place.stops.StopsDraft
import com.juanpablo0612.carpool.presentation.place.stops.stopsEditorItems
import com.juanpablo0612.carpool.presentation.trip.TripError
import com.juanpablo0612.carpool.presentation.trip.asStringResource
import com.juanpablo0612.carpool.presentation.trip.publish.components.TripSeatsSection
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.DetailSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.ErrorState
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.components.SectionHeader
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.ContentWidth
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.BottomBarInsets
import com.juanpablo0612.carpool.presentation.ui.util.CenteredContent
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.ScreenInsets
import com.juanpablo0612.carpool.presentation.ui.util.ScreenPreviews
import com.juanpablo0612.carpool.presentation.ui.util.WindowLayout
import com.juanpablo0612.carpool.presentation.ui.util.centeredContent
import com.juanpablo0612.carpool.presentation.ui.util.departureDayLabel
import com.juanpablo0612.carpool.presentation.ui.util.formatTime
import com.juanpablo0612.carpool.presentation.ui.util.plusHorizontal
import com.juanpablo0612.carpool.presentation.ui.util.rememberNowMs
import com.juanpablo0612.carpool.presentation.ui.util.rememberWindowLayout
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.discard_changes_body
import enrutadoseia.composeapp.generated.resources.discard_changes_confirm
import enrutadoseia.composeapp.generated.resources.discard_changes_title
import enrutadoseia.composeapp.generated.resources.edit_trip_confirmed_seats
import enrutadoseia.composeapp.generated.resources.edit_trip_fixed_hint
import enrutadoseia.composeapp.generated.resources.edit_trip_locked_stops_hint
import enrutadoseia.composeapp.generated.resources.edit_trip_notify_note
import enrutadoseia.composeapp.generated.resources.edit_trip_save_button
import enrutadoseia.composeapp.generated.resources.edit_trip_seats_confirmed_max
import enrutadoseia.composeapp.generated.resources.edit_trip_stop_gets_off
import enrutadoseia.composeapp.generated.resources.edit_trip_stop_gets_on
import enrutadoseia.composeapp.generated.resources.edit_trip_stop_passengers
import enrutadoseia.composeapp.generated.resources.edit_trip_title
import enrutadoseia.composeapp.generated.resources.info_24px
import enrutadoseia.composeapp.generated.resources.publish_trip_route_section
import enrutadoseia.composeapp.generated.resources.relative_day_at_time
import enrutadoseia.composeapp.generated.resources.route_from_to
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import kotlin.time.Clock

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditTripScreen(
    viewModel: EditTripViewModel,
    onBackClick: () -> Unit,
    onTripUpdated: () -> Unit,
    onNavigateToAddPlace: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            EditTripEvent.NavigateBack -> onBackClick()
            EditTripEvent.TripUpdated -> onTripUpdated()
        }
    }

    // Route system back through the same dirty-check as the top-bar arrow.
    BackHandler(enabled = state.selectionTarget == null) {
        viewModel.onAction(EditTripAction.OnBackClick)
    }

    StopSelectionHost(
        selectionTarget = state.selectionTarget,
        onPlaceSelected = { viewModel.onAction(EditTripAction.OnPlaceSelected(it)) },
        onCancelSelection = { viewModel.onAction(EditTripAction.OnCancelSelection) },
        onNavigateToAddPlace = onNavigateToAddPlace,
    ) {
        EditTripContent(state = state, onAction = viewModel::onAction)
    }
}

@Composable
fun EditTripContent(
    state: EditTripUiState,
    onAction: (EditTripAction) -> Unit,
    layout: WindowLayout = rememberWindowLayout(),
    now: Long = rememberNowMs(),
) {
    if (state.showDiscardConfirm) {
        ConfirmDialog(
            title = stringResource(Res.string.discard_changes_title),
            description = stringResource(Res.string.discard_changes_body),
            confirmText = stringResource(Res.string.discard_changes_confirm),
            onConfirm = { onAction(EditTripAction.OnConfirmDiscard) },
            onDismiss = { onAction(EditTripAction.OnDismissDiscard) },
            isDestructive = true,
        )
    }

    val trip = state.trip
    val lockedWaypointTags = state.stopUsers.mapValues { (_, users) -> users.tag() }
    Scaffold(
        contentWindowInsets = ScreenInsets,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CarpoolBackTopBar(
                title = stringResource(Res.string.edit_trip_title),
                subtitle = trip?.let { stringResource(Res.string.route_from_to, it.origin.name, it.destination.name) },
                onBack = { onAction(EditTripAction.OnBackClick) },
            )
        },
        bottomBar = {
            if (trip != null) {
                Surface(color = MaterialTheme.colorScheme.surfaceContainerLowest, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        // Stays enabled: tapping it with problems shows them next to each field.
                        PrimaryButton(
                            text = stringResource(Res.string.edit_trip_save_button),
                            onClick = { onAction(EditTripAction.OnSaveClick) },
                            isLoading = state.isSaving,
                            modifier = Modifier
                                .windowInsetsPadding(BottomBarInsets)
                                .centeredContent(ContentWidth.form)
                                .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.md),
                        )
                    }
                }
            }
        },
    ) { padding ->
        when {
            state.isLoading -> DetailSkeleton(modifier = Modifier.fillMaxSize().padding(padding))
            trip == null -> ErrorState(
                description = stringResource((state.loadError ?: TripError.TripNotFound).asStringResource()),
                onRetry = { onAction(EditTripAction.OnRetry) },
                modifier = Modifier.fillMaxSize().padding(padding).padding(Spacing.lg),
            )
            else -> CenteredContent(
                ContentWidth.form,
                modifier = Modifier.fillMaxSize().padding(padding),
                gutter = 0.dp,
            ) { margin ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = Spacing.lg).plusHorizontal(margin),
                ) {
                    item(key = "fixed_hint") {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                            modifier = Modifier.padding(
                                start = Spacing.screenHorizontal,
                                end = Spacing.screenHorizontal,
                                top = Spacing.md,
                            ),
                        ) {
                            Text(
                                text = stringResource(
                                    Res.string.relative_day_at_time,
                                    departureDayLabel(trip.departureTime, now),
                                    formatTime(trip.departureTime),
                                ),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.semantics { heading() },
                            )
                            Text(
                                text = stringResource(Res.string.edit_trip_fixed_hint),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    item(key = "route_header") {
                        SectionHeader(
                            title = stringResource(Res.string.publish_trip_route_section),
                            modifier = Modifier.padding(top = Spacing.md),
                        )
                    }
                    stopsEditorItems(
                        stops = state.stops,
                        onOriginClick = {},
                        onDestinationClick = {},
                        onEditWaypoint = { onAction(EditTripAction.OnEditWaypointClick(it)) },
                        onRemoveWaypoint = { onAction(EditTripAction.OnRemoveWaypoint(it)) },
                        onAddWaypoint = { onAction(EditTripAction.OnAddWaypointClick) },
                        endpointsLocked = true,
                        lockedWaypointTags = lockedWaypointTags,
                    )
                    if (state.stops.waypoints.any { it.name in lockedWaypointTags }) {
                        item(key = "locked_stops_hint") {
                            Text(
                                text = stringResource(Res.string.edit_trip_locked_stops_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.xs),
                            )
                        }
                    }
                    fieldErrorItem("stops_error", state.error, TripError.StopInUse)

                    item(key = "seats") {
                        TripSeatsSection(
                            seatCount = state.seatCount,
                            selectedVehicle = state.vehicle,
                            onChange = { onAction(EditTripAction.OnSetSeats(it)) },
                            minSeats = state.minSeats,
                            supportingText = seatsSupportingText(trip.confirmedSeats, state.vehicle?.seatsAvailable),
                            // With seats confirmed, the supporting text already carries the cap.
                            showCapacityHelper = trip.confirmedSeats == 0,
                            modifier = Modifier.padding(
                                start = Spacing.screenHorizontal,
                                end = Spacing.screenHorizontal,
                                top = Spacing.xl,
                            ),
                        )
                    }
                    fieldErrorItem("seats_error", state.error, TripError.SeatsOutOfRange, TripError.SeatsBelowConfirmed)

                    item(key = "notify_note") {
                        NotifyPassengersNote(
                            modifier = Modifier.padding(
                                start = Spacing.screenHorizontal,
                                end = Spacing.screenHorizontal,
                                top = Spacing.xl,
                            ),
                        )
                    }

                    val generalError = state.error?.takeUnless { it in FIELD_ERRORS }
                    if (generalError != null) {
                        item(key = "error") {
                            ErrorMessage(
                                message = stringResource(generalError.asStringResource()),
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
    }
}

/** "N already confirmed · up to M", or just the confirmed count if the car couldn't be read. */
@Composable
private fun seatsSupportingText(confirmedSeats: Int, capacity: Int?): String? = when {
    confirmedSeats <= 0 -> null
    capacity == null -> pluralStringResource(Res.plurals.edit_trip_confirmed_seats, confirmedSeats, confirmedSeats)
    else -> pluralStringResource(Res.plurals.edit_trip_seats_confirmed_max, confirmedSeats, confirmedSeats, capacity)
}

/** Stop changes reach passengers as a notification, so the driver knows before saving. */
@Composable
private fun NotifyPassengersNote(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.info_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = stringResource(Res.string.edit_trip_notify_note),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** The note under a locked stop saying who chose it. */
@Composable
private fun StopUsers.tag(): String = when (this) {
    is StopUsers.One -> stringResource(
        if (isDropoff) Res.string.edit_trip_stop_gets_off else Res.string.edit_trip_stop_gets_on,
        firstName,
    )
    is StopUsers.Several -> pluralStringResource(Res.plurals.edit_trip_stop_passengers, count, count)
}

/** Errors shown next to the field they concern rather than at the end of the form. */
private val FIELD_ERRORS = setOf(TripError.StopInUse, TripError.SeatsOutOfRange, TripError.SeatsBelowConfirmed)

/** An inline error under a section, when [error] is one of [relevant]. */
private fun LazyListScope.fieldErrorItem(key: String, error: TripError?, vararg relevant: TripError) {
    if (error == null || error !in relevant) return
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

private val previewNow = Clock.System.now().toEpochMilliseconds()

private val previewTrip = Trip(
    id = "t1",
    routeId = "",
    driverId = "d1",
    vehicleId = "v1",
    origin = Place(name = "Casa", address = "Calle 10 #20-30", latitude = 6.2, longitude = -75.6),
    destination = Place.EIA_LAS_PALMAS,
    waypoints = listOf(Place(name = "Viva Envigado", address = "Carrera 48", latitude = 6.18, longitude = -75.59)),
    departureTime = previewNow + 3 * 3_600_000L,
    seatCount = 3,
    status = TripStatus.Active,
    confirmedSeats = 2,
)

@ScreenPreviews
@Composable
private fun EditTripPreview() {
    CarpoolTheme {
        EditTripContent(
            state = EditTripUiState(
                isLoading = false,
                trip = previewTrip,
                vehicle = Vehicle(
                    id = "v1",
                    driverId = "d1",
                    brand = "Mazda",
                    model = "3",
                    licensePlate = "ABC123",
                    color = "Gris",
                    year = 2020,
                    seatsAvailable = 4,
                ),
                seatCount = 3,
                stops = StopsDraft.of(previewTrip),
                stopUsers = mapOf("Viva Envigado" to StopUsers.One(firstName = "Laura", isDropoff = false)),
            ),
            onAction = {},
            now = previewNow,
        )
    }
}
