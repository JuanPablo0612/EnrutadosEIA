package com.juanpablo0612.carpool.presentation.place.stops

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import com.juanpablo0612.carpool.domain.place.model.Place
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

    if (selectionTarget != null) {
        PlaceSelectorContent(
            state = selectorState,
            onAction = activeSelector::onAction,
            onPlaceSelected = select,
            onBack = onCancelSelection,
        )
    } else {
        content()
    }
}
