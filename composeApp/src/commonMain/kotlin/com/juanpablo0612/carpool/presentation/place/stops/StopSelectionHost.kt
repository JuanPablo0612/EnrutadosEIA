package com.juanpablo0612.carpool.presentation.place.stops

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.presentation.place.picker.MapPickerContent
import com.juanpablo0612.carpool.presentation.place.picker.MapPickerUiState
import com.juanpablo0612.carpool.presentation.place.picker.MapPickerViewModel
import com.juanpablo0612.carpool.presentation.place.selector.MapPickRequest
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorAction
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorContent
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorEvent
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorViewModel
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Shows the place selector in place of [content] while [selectionTarget] is set, and reports the
 * chosen place. The selector is an inline content swap rather than a back-stack entry, so system
 * back closes it instead of leaving the form.
 *
 * Each target kind gets its own keyed selector ViewModel (scoped to the hosting back-stack entry),
 * so the selector's mode-dependent title and search don't bleed across origin/destination/stops.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StopSelectionHost(
    selectionTarget: SelectionTarget?,
    onPlaceSelected: (Place) -> Unit,
    onCancelSelection: () -> Unit,
    onNavigateToAddPlace: () -> Unit,
    content: @Composable () -> Unit,
) {
    val originSelector: PlaceSelectorViewModel = koinViewModel(key = "origin") { parametersOf("ORIGIN") }
    val destinationSelector: PlaceSelectorViewModel = koinViewModel(key = "destination") { parametersOf("DESTINATION") }
    val waypointSelector: PlaceSelectorViewModel = koinViewModel(key = "waypoint") { parametersOf("WAYPOINT") }

    val activeSelector = when (selectionTarget) {
        SelectionTarget.Destination -> destinationSelector
        is SelectionTarget.EditWaypoint, SelectionTarget.NewWaypoint -> waypointSelector
        SelectionTarget.Origin, null -> originSelector
    }
    val selectorState by activeSelector.state.collectAsState()

    val select: (Place) -> Unit = { place ->
        onPlaceSelected(place)
        activeSelector.onAction(PlaceSelectorAction.OnDismiss)
    }
    // Row taps (saved/campus place) call select directly, but "use current location" and
    // search-suggestion taps only emit PlaceSelectorEvent.PlaceSelected, so they must be observed.
    ObserveAsEvents(activeSelector.events) { event ->
        when (event) {
            is PlaceSelectorEvent.PlaceSelected -> select(event.place)
            PlaceSelectorEvent.NavigateToAddPlace -> onNavigateToAddPlace()
            PlaceSelectorEvent.Dismiss -> Unit
        }
    }

    BackHandler(enabled = selectionTarget != null, onBack = onCancelSelection)

    val mapPick = selectorState.mapPick
    // Declared after the selector's handler so it wins: back from the map returns to the list.
    BackHandler(enabled = selectionTarget != null && mapPick != null) {
        activeSelector.onAction(PlaceSelectorAction.OnCancelMapPick)
    }

    if (selectionTarget != null && mapPick != null) {
        InlineMapPicker(
            request = mapPick,
            selectorKey = selectorState.mode.toString(),
            onPicked = { picked ->
                activeSelector.onAction(
                    PlaceSelectorAction.OnMapPointPicked(
                        latitude = picked.pickedCoordinates.latitude,
                        longitude = picked.pickedCoordinates.longitude,
                        placeName = picked.pickedPlaceName,
                        address = picked.resolvedAddress,
                    )
                )
            },
            onBack = { activeSelector.onAction(PlaceSelectorAction.OnCancelMapPick) },
        )
    } else if (selectionTarget != null) {
        PlaceSelectorContent(
            state = selectorState,
            onAction = activeSelector::onAction,
            onPlaceSelected = select,
            onBack = onCancelSelection,
            showChooseOnMap = true,
        )
    } else {
        content()
    }
}

/**
 * The map picker shown in place of the selector. It is inline, like the selector itself, rather
 * than the [com.juanpablo0612.carpool.presentation.navigation.Route.MapPicker] destination, so
 * the picked point comes back without every form that hosts stops wiring a navigation result.
 */
@Composable
private fun InlineMapPicker(
    request: MapPickRequest,
    selectorKey: String,
    onPicked: (MapPickerUiState) -> Unit,
    onBack: () -> Unit,
) {
    // Keyed by opening, so each "choose on map" starts from a fresh pin.
    val viewModel: MapPickerViewModel = koinViewModel(key = "map_pick_${selectorKey}_${request.session}") {
        parametersOf(request.initialLatitude, request.initialLongitude)
    }
    val state by viewModel.state.collectAsState()
    MapPickerContent(
        state = state,
        onPinDragged = viewModel::onPinDragged,
        onPoiSelected = viewModel::onPoiSelected,
        onMyLocationClick = viewModel::onMyLocationClick,
        onConfirm = { onPicked(state) },
        onBack = onBack,
        onDismissError = viewModel::onDismissError,
    )
}
