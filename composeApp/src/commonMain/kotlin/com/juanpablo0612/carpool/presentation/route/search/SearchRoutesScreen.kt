package com.juanpablo0612.carpool.presentation.route.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorAction
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorContent
import com.juanpablo0612.carpool.presentation.place.selector.PlaceSelectorViewModel
import com.juanpablo0612.carpool.presentation.route.search.components.DateTimeBottomSheet
import com.juanpablo0612.carpool.presentation.route.search.components.FiltersBottomSheet
import com.juanpablo0612.carpool.presentation.route.search.components.SearchCard
import com.juanpablo0612.carpool.presentation.route.search.components.TripResultCard
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTopBar
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.role_switch_to_driver
import enrutadoseia.composeapp.generated.resources.role_selector_passenger_title
import enrutadoseia.composeapp.generated.resources.search_24px
import enrutadoseia.composeapp.generated.resources.search_adjust_button
import enrutadoseia.composeapp.generated.resources.search_empty_subtitle
import enrutadoseia.composeapp.generated.resources.search_empty_title
import enrutadoseia.composeapp.generated.resources.passenger_home_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SearchRoutesScreen(
    viewModel: SearchRoutesViewModel,
    user: User,
    isDualRole: Boolean,
    onSwitchRole: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToTripDetail: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    val originSelectorViewModel: PlaceSelectorViewModel = koinViewModel(key = "origin") { parametersOf("ORIGIN") }
    val destinationSelectorViewModel: PlaceSelectorViewModel = koinViewModel(key = "destination") { parametersOf("DESTINATION") }
    val originSelectorState by originSelectorViewModel.state.collectAsState()
    val destinationSelectorState by destinationSelectorViewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is SearchRoutesEvent.NavigateToTripDetail -> onNavigateToTripDetail(event.tripId)
        }
    }

    when (state.selectionTarget) {
        "ORIGIN" -> PlaceSelectorContent(
            state = originSelectorState,
            onAction = originSelectorViewModel::onAction,
            onPlaceSelected = { place ->
                viewModel.onAction(SearchRoutesAction.OnPlaceSelected(place))
                originSelectorViewModel.onAction(PlaceSelectorAction.OnDismiss)
            },
            onBack = { viewModel.onAction(SearchRoutesAction.OnCancelPlaceSelection) },
            onNavigateToAddPlace = {}
        )

        "DESTINATION" -> PlaceSelectorContent(
            state = destinationSelectorState,
            onAction = destinationSelectorViewModel::onAction,
            onPlaceSelected = { place ->
                viewModel.onAction(SearchRoutesAction.OnPlaceSelected(place))
                destinationSelectorViewModel.onAction(PlaceSelectorAction.OnDismiss)
            },
            onBack = { viewModel.onAction(SearchRoutesAction.OnCancelPlaceSelection) },
            onNavigateToAddPlace = {}
        )

        else -> SearchRoutesContent(
            state = state,
            user = user,
            isDualRole = isDualRole,
            onAction = viewModel::onAction,
            onSwitchRole = onSwitchRole,
            onNavigateToProfile = onNavigateToProfile
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRoutesContent(
    state: SearchRoutesUiState,
    user: User,
    isDualRole: Boolean,
    onAction: (SearchRoutesAction) -> Unit,
    onSwitchRole: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val filtersSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dateTimeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            CarpoolTopBar(
                title = stringResource(Res.string.passenger_home_title),
                user = user,
                isDualRole = isDualRole,
                switchRoleLabel = stringResource(Res.string.role_switch_to_driver),
                onAvatarClick = onNavigateToProfile,
                onRoleToggle = if (isDualRole) onSwitchRole else null
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchCard(
                state = state,
                onAction = onAction,
                modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.sm)
            )

            HorizontalDivider()

            when {
                state.isLoading -> ListSkeleton(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Spacing.lg)
                )

                state.isSearching -> ListSkeleton(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Spacing.lg)
                )

                state.hasSearched && state.results.isEmpty() -> EmptyState(
                    icon = vectorResource(Res.drawable.search_24px),
                    title = stringResource(Res.string.search_empty_title),
                    description = stringResource(Res.string.search_empty_subtitle),
                    modifier = Modifier.fillMaxSize(),
                    primaryAction = com.juanpablo0612.carpool.presentation.ui.components.ActionButton(
                        label = stringResource(Res.string.search_adjust_button),
                        onClick = { onAction(SearchRoutesAction.OnShowFilters) }
                    )
                )

                state.results.isNotEmpty() -> LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.md)
                ) {
                    items(state.results, key = { it.trip.id }) { result ->
                        TripResultCard(
                            result = result,
                            onClick = { onAction(SearchRoutesAction.OnTripClick(result.trip.id)) }
                        )
                    }
                }

                else -> Spacer(modifier = Modifier.fillMaxSize())
            }
        }
    }

    if (state.showFiltersSheet) {
        FiltersBottomSheet(
            filters = state.filters,
            sheetState = filtersSheetState,
            onApply = { onAction(SearchRoutesAction.OnFiltersChanged(it)) },
            onDismiss = { onAction(SearchRoutesAction.OnDismissFilters) }
        )
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

private val previewUser = User(
    id = "1",
    email = "pasajero@eia.edu.co",
    name = "Maria García",
    isEmailVerified = true,
    isPassenger = true,
    isDriver = false
)

@Preview
@Composable
private fun SearchRoutesEmptyPreview() {
    CarpoolTheme {
        SearchRoutesContent(
            state = SearchRoutesUiState(isLoading = false),
            user = previewUser,
            isDualRole = false,
            onAction = {},
            onSwitchRole = {},
            onNavigateToProfile = {}
        )
    }
}
