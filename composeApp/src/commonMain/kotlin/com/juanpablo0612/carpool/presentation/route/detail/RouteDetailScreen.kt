package com.juanpablo0612.carpool.presentation.route.detail

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.presentation.place.stops.StopSelectionHost
import com.juanpablo0612.carpool.presentation.place.stops.StopsDraft
import com.juanpablo0612.carpool.presentation.route.create.CreateRouteUiState
import com.juanpablo0612.carpool.presentation.route.detail.components.RouteEditActions
import com.juanpablo0612.carpool.presentation.route.detail.components.RouteEditDialogs
import com.juanpablo0612.carpool.presentation.route.detail.components.RouteDetailBody
import com.juanpablo0612.carpool.presentation.route.detail.components.RouteReadActions
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import com.juanpablo0612.carpool.presentation.ui.components.DetailSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.ErrorState
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.ScreenInsets
import com.juanpablo0612.carpool.presentation.ui.util.ScreenPreviews
import com.juanpablo0612.carpool.presentation.ui.util.WindowLayout
import com.juanpablo0612.carpool.presentation.ui.util.rememberWindowLayout
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.cd_delete_route
import enrutadoseia.composeapp.generated.resources.cd_edit_route
import enrutadoseia.composeapp.generated.resources.delete_24px
import enrutadoseia.composeapp.generated.resources.edit_24px
import enrutadoseia.composeapp.generated.resources.route_delete_confirm_button
import enrutadoseia.composeapp.generated.resources.route_delete_confirm_description
import enrutadoseia.composeapp.generated.resources.route_delete_confirm_title
import enrutadoseia.composeapp.generated.resources.route_detail_title
import enrutadoseia.composeapp.generated.resources.route_edit_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RouteDetailScreen(
    viewModel: RouteDetailViewModel,
    onBackClick: () -> Unit,
    onNavigateToAddPlace: () -> Unit,
    onNavigateToPublishTrip: (String) -> Unit,
    onNavigateToPublishWeek: (String) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RouteDetailEvent.NavigateBack -> onBackClick()
            is RouteDetailEvent.NavigateToPublishTrip -> onNavigateToPublishTrip(event.routeId)
            is RouteDetailEvent.NavigateToPublishWeek -> onNavigateToPublishWeek(event.routeId)
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

    val selectionTarget = state.draft?.selectionTarget

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
        RouteDetailContent(state = state, onAction = viewModel::onAction)
    }
}

/**
 * Read and edit mode share this scaffold: the route map stays at the top (or beside the details on
 * wide windows) and follows the draft's stops while editing, and only the fields below switch
 * between read-only and editable.
 */
@Composable
internal fun RouteDetailContent(
    state: RouteDetailUiState,
    onAction: (RouteDetailAction) -> Unit,
    layout: WindowLayout = rememberWindowLayout(),
) {
    val route = state.route
    val draft = state.draft?.takeIf { state.isEditing }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    if (draft != null) {
        RouteEditDialogs(
            draft = draft,
            showTimePicker = showTimePicker,
            showDiscardConfirm = state.showDiscardEditConfirm,
            onDismissTimePicker = { showTimePicker = false },
            onAction = onAction,
        )
    }

    Scaffold(
        contentWindowInsets = ScreenInsets,
        topBar = {
            CarpoolBackTopBar(
                title = when {
                    draft != null -> stringResource(Res.string.route_edit_title)
                    else -> route?.name?.takeIf { it.isNotBlank() } ?: stringResource(Res.string.route_detail_title)
                },
                onBack = {
                    onAction(if (draft != null) RouteDetailAction.OnCancelEdit else RouteDetailAction.OnBackClick)
                },
                actions = { if (route != null && draft == null) ReadModeActions(onAction) },
            )
        },
        bottomBar = {
            when {
                draft != null -> RouteEditActions(
                    canSave = draft.isValid && !state.isSaved,
                    isSaving = state.isSaving,
                    onAction = onAction,
                )
                route != null -> RouteReadActions(route = route, isDuplicating = state.isDuplicating, onAction = onAction)
            }
        }
    ) { padding ->
        when {
            state.isLoading -> DetailSkeleton(modifier = Modifier.fillMaxSize().padding(padding))
            route == null -> ErrorState(
                description = stringResource((state.error ?: RouteDetailError.NotFound).asStringResource()),
                onRetry = { onAction(RouteDetailAction.OnRetry) },
                modifier = Modifier.fillMaxSize().padding(padding).padding(Spacing.lg)
            )
            else -> RouteDetailBody(
                route = route,
                draft = draft,
                state = state,
                layout = layout,
                onAction = onAction,
                onPickTime = { showTimePicker = true },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .imePadding(),
            )
        }
    }
}

@Composable
private fun ReadModeActions(onAction: (RouteDetailAction) -> Unit) {
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

private val previewRoute = Route(
    id = "r1",
    driverId = "d1",
    name = "Ida a clase",
    origin = Place(name = "Casa", address = "Calle 10 #20-30", latitude = 6.2, longitude = -75.6),
    destination = Place(name = "EIA", address = "Cl. 49 Sur #50-90", latitude = 6.18, longitude = -75.59),
    waypoints = listOf(
        Place(name = "Parada 1", address = "Carrera 43A", latitude = 6.21, longitude = -75.57)
    )
)

@ScreenPreviews
@Composable
private fun RouteDetailReadPreview() {
    CarpoolTheme {
        RouteDetailContent(
            state = RouteDetailUiState(isLoading = false, route = previewRoute, tripsPublished = 5),
            onAction = {}
        )
    }
}

@ScreenPreviews
@Composable
private fun RouteDetailEditPreview() {
    CarpoolTheme {
        RouteDetailContent(
            state = RouteDetailUiState(
                isLoading = false,
                route = previewRoute,
                isEditing = true,
                draft = CreateRouteUiState(name = previewRoute.name, stops = StopsDraft.of(previewRoute)),
            ),
            onAction = {}
        )
    }
}
