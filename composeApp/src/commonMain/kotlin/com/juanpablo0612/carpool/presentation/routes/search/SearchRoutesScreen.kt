package com.juanpablo0612.carpool.presentation.routes.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.presentation.places.selector.PlaceSelectorAction
import com.juanpablo0612.carpool.presentation.places.selector.PlaceSelectorContent
import com.juanpablo0612.carpool.presentation.places.selector.PlaceSelectorViewModel
import com.juanpablo0612.carpool.presentation.routes.search.components.TripResultCard
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTopBar
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.filter_list_24px
import enrutadoseia.composeapp.generated.resources.role_selector_passenger_title
import enrutadoseia.composeapp.generated.resources.search_24px
import enrutadoseia.composeapp.generated.resources.search_adjust_button
import enrutadoseia.composeapp.generated.resources.search_button
import enrutadoseia.composeapp.generated.resources.search_date_placeholder
import enrutadoseia.composeapp.generated.resources.search_destination_placeholder
import enrutadoseia.composeapp.generated.resources.search_empty_subtitle
import enrutadoseia.composeapp.generated.resources.search_empty_title
import enrutadoseia.composeapp.generated.resources.search_filter_female_driver
import enrutadoseia.composeapp.generated.resources.search_filter_female_driver_coming_soon
import enrutadoseia.composeapp.generated.resources.search_filter_max_contribution
import enrutadoseia.composeapp.generated.resources.search_filters_button
import enrutadoseia.composeapp.generated.resources.search_filters_title
import enrutadoseia.composeapp.generated.resources.search_origin_placeholder
import enrutadoseia.composeapp.generated.resources.swap_horiz_24px
import enrutadoseia.composeapp.generated.resources.passenger_home_title
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
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
                currentRoleLabel = stringResource(Res.string.role_selector_passenger_title),
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

@Composable
private fun SearchCard(
    state: SearchRoutesUiState,
    onAction: (SearchRoutesAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.origin?.name ?: "",
                    onValueChange = {},
                    enabled = false,
                    placeholder = { Text(stringResource(Res.string.search_origin_placeholder)) },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onAction(SearchRoutesAction.OnPickOrigin) },
                    singleLine = true
                )
                IconButton(onClick = { onAction(SearchRoutesAction.OnSwapPlaces) }) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.swap_horiz_24px),
                        contentDescription = null,
                        modifier = Modifier.rotate(90f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            OutlinedTextField(
                value = state.destination?.name ?: "",
                onValueChange = {},
                enabled = false,
                placeholder = { Text(stringResource(Res.string.search_destination_placeholder)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(SearchRoutesAction.OnPickDestination) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            OutlinedTextField(
                value = if (state.selectedEpochMs != null) formatEpochShort(state.selectedEpochMs) else "",
                onValueChange = {},
                enabled = false,
                placeholder = { Text(stringResource(Res.string.search_date_placeholder)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(SearchRoutesAction.OnShowDateTimeSheet) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = { onAction(SearchRoutesAction.OnShowFilters) },
                    label = { Text(stringResource(Res.string.search_filters_button)) },
                    leadingIcon = {
                        Icon(
                            imageVector = vectorResource(Res.drawable.filter_list_24px),
                            contentDescription = null
                        )
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { onAction(SearchRoutesAction.OnSearchClick) },
                    enabled = !state.isSearching
                ) {
                    Text(stringResource(Res.string.search_button))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltersBottomSheet(
    filters: SearchFilters,
    sheetState: androidx.compose.material3.SheetState,
    onApply: (SearchFilters) -> Unit,
    onDismiss: () -> Unit
) {
    var maxContrib by remember { mutableStateOf(filters.maxContribution?.toString() ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
                .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Text(
                text = stringResource(Res.string.search_filters_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            OutlinedTextField(
                value = maxContrib,
                onValueChange = { maxContrib = it.filter { c -> c.isDigit() } },
                label = { Text(stringResource(Res.string.search_filter_max_contribution)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                prefix = { Text("$") }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(stringResource(Res.string.search_filter_female_driver), style = MaterialTheme.typography.bodyMedium)
                    Text(
                        stringResource(Res.string.search_filter_female_driver_coming_soon),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = false, onCheckedChange = {}, enabled = false)
            }

            Button(
                onClick = {
                    onApply(
                        SearchFilters(
                            maxContribution = maxContrib.toIntOrNull()
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.search_button))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTimeBottomSheet(
    currentEpochMs: Long?,
    currentTolerance: Int,
    sheetState: androidx.compose.material3.SheetState,
    onConfirm: (Long?, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTolerance by remember { mutableIntStateOf(currentTolerance) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
                .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Text(
                text = stringResource(Res.string.search_date_placeholder),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            Text(
                "Tolerancia",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                listOf(15, 30, 60).forEach { tol ->
                    AssistChip(
                        onClick = { selectedTolerance = tol },
                        label = { Text("$tol min") },
                        colors = if (selectedTolerance == tol) {
                            androidx.compose.material3.AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        } else androidx.compose.material3.AssistChipDefaults.assistChipColors()
                    )
                }
            }

            Button(
                onClick = { onConfirm(currentEpochMs, selectedTolerance) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.search_button))
            }
        }
    }
}

internal fun formatEpochShort(epochMs: Long): String {
    val instant = Instant.fromEpochMilliseconds(epochMs)
    val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = local.hour.toString().padStart(2, '0')
    val minute = local.minute.toString().padStart(2, '0')
    @Suppress("DEPRECATION")
    val day = local.dayOfMonth.toString().padStart(2, '0')
    @Suppress("DEPRECATION")
    val month = local.monthNumber.toString().padStart(2, '0')
    return "$day/$month · $hour:$minute"
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
