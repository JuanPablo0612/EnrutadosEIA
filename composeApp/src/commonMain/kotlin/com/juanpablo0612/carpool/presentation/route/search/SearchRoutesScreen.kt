package com.juanpablo0612.carpool.presentation.route.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.CampusDirection
import com.juanpablo0612.carpool.domain.trip.model.TripMeetingStop
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorAction
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorContent
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorEvent
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorViewModel
import com.juanpablo0612.carpool.presentation.route.search.components.DateTimeBottomSheet
import com.juanpablo0612.carpool.presentation.route.search.components.SearchEmptyState
import com.juanpablo0612.carpool.presentation.route.search.components.SearchHeader
import com.juanpablo0612.carpool.presentation.route.search.components.TripResultCard
import com.juanpablo0612.carpool.presentation.trip.asStringResource
import com.juanpablo0612.carpool.presentation.ui.components.ErrorState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.ScreenInsets
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.search_results_count
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchRoutesScreen(
    viewModel: SearchRoutesViewModel,
    onNavigateToTripDetail: (tripId: String, meetingStop: TripMeetingStop?) -> Unit,
    onNavigateToAddPlace: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // The passenger's place is the trip's origin going to campus and its destination leaving it,
    // so each direction gets the selector variant titled for that end.
    val pickupSelectorViewModel: PlaceSelectorViewModel = koinViewModel(key = "origin") { parametersOf("ORIGIN") }
    val dropoffSelectorViewModel: PlaceSelectorViewModel = koinViewModel(key = "destination") { parametersOf("DESTINATION") }
    val selectorViewModel = when (state.direction) {
        CampusDirection.ToCampus -> pickupSelectorViewModel
        CampusDirection.FromCampus -> dropoffSelectorViewModel
    }
    val selectorState by selectorViewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is SearchRoutesEvent.NavigateToTripDetail -> onNavigateToTripDetail(event.tripId, event.meetingStop)
        }
    }

    val onPlaceSelected: (Place) -> Unit = { place ->
        viewModel.onAction(SearchRoutesAction.OnPlaceSelected(place))
        selectorViewModel.onAction(PlaceSelectorAction.OnDismiss)
    }
    // Row taps (saved/campus place) call onPlaceSelected directly, but "use current location"
    // and search-suggestion taps only emit PlaceSelectorEvent.PlaceSelected, so they must be
    // observed here — otherwise those taps do nothing and the emit suspends with no collector.
    listOf(pickupSelectorViewModel, dropoffSelectorViewModel).forEach { selector ->
        ObserveAsEvents(selector.events) { event ->
            when (event) {
                is PlaceSelectorEvent.PlaceSelected -> onPlaceSelected(event.place)
                PlaceSelectorEvent.NavigateToAddPlace -> onNavigateToAddPlace()
                PlaceSelectorEvent.Dismiss -> Unit
            }
        }
    }

    // The selector is an inline content swap, not a real back-stack entry — without this, system
    // back while it's open leaves the tab and discards the in-progress search.
    BackHandler(enabled = state.isPickingPlace) {
        viewModel.onAction(SearchRoutesAction.OnCancelPlaceSelection)
    }

    if (state.isPickingPlace) {
        PlaceSelectorContent(
            state = selectorState,
            onAction = selectorViewModel::onAction,
            onPlaceSelected = onPlaceSelected,
            onBack = { viewModel.onAction(SearchRoutesAction.OnCancelPlaceSelection) },
        )
    } else {
        SearchRoutesContent(state = state, onAction = viewModel::onAction)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRoutesContent(
    state: SearchRoutesUiState,
    onAction: (SearchRoutesAction) -> Unit,
) {
    val dateTimeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(contentWindowInsets = ScreenInsets, containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchHeader(state = state, onAction = onAction)

            when {
                state.loadError != null -> ErrorState(
                    description = stringResource(state.loadError.asStringResource()),
                    onRetry = { onAction(SearchRoutesAction.RetryLoad) },
                    modifier = Modifier.fillMaxSize(),
                )

                state.isLoading || !state.hasSearched -> ListSkeleton(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = Spacing.lg)
                )

                state.results.isEmpty() -> SearchEmptyState(
                    relaxation = state.relaxation,
                    onAction = onAction,
                    modifier = Modifier.fillMaxSize(),
                )

                else -> SearchResults(results = state.results, onAction = onAction)
            }
        }
    }

    if (state.showDateTimeSheet) {
        DateTimeBottomSheet(
            currentEpochMs = state.selectedEpochMs,
            currentTolerance = state.toleranceMinutes,
            sheetState = dateTimeSheetState,
            onConfirm = { epochMs, tolerance ->
                onAction(SearchRoutesAction.OnDateTimeChanged(epochMs, tolerance))
            },
            onDismiss = { onAction(SearchRoutesAction.OnDismissDateTimeSheet) }
        )
    }
}

@Composable
private fun SearchResults(results: List<TripResult>, onAction: (SearchRoutesAction) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = PaddingValues(horizontal = Spacing.screenHorizontal, vertical = Spacing.lg),
    ) {
        item(key = "summary") {
            // Announced politely so screen-reader users hear the count change as they adjust
            // the search, without having to look for it.
            Text(
                text = pluralStringResource(Res.plurals.search_results_count, results.size, results.size),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
        items(results, key = { it.trip.id }) { result ->
            TripResultCard(
                result = result,
                onClick = { onAction(SearchRoutesAction.OnTripClick(result.trip.id)) },
            )
        }
    }
}

@Preview
@Composable
private fun SearchRoutesEmptyPreview() {
    CarpoolTheme {
        SearchRoutesContent(
            state = SearchRoutesUiState(isLoading = false, hasSearched = true),
            onAction = {},
        )
    }
}
