package com.juanpablo0612.carpool.presentation.route.detail.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.place.model.Coordinates
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.presentation.place.add.components.MapRoutePreview
import com.juanpablo0612.carpool.presentation.place.stops.StopsDraft
import com.juanpablo0612.carpool.presentation.route.create.CreateRouteUiState
import com.juanpablo0612.carpool.presentation.route.detail.RouteDetailAction
import com.juanpablo0612.carpool.presentation.route.detail.RouteDetailUiState
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.theme.ContentWidth
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.CenteredContent
import com.juanpablo0612.carpool.presentation.ui.util.WindowLayout
import com.juanpablo0612.carpool.presentation.ui.util.mediaPreviewSize
import com.juanpablo0612.carpool.presentation.ui.util.plusHorizontal
import org.jetbrains.compose.resources.stringResource

/**
 * The route map and, below or beside it, either the saved route or the edit form. The map is the
 * same item in both modes, so switching modes keeps it in place.
 */
@Composable
internal fun RouteDetailBody(
    route: Route,
    draft: CreateRouteUiState?,
    state: RouteDetailUiState,
    layout: WindowLayout,
    onAction: (RouteDetailAction) -> Unit,
    onPickTime: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // While editing, the map previews the stops being chosen rather than the saved ones.
    val stops = draft?.stops ?: StopsDraft.of(route)
    val markers = remember(stops) {
        (listOfNotNull(stops.origin) + stops.waypoints + listOfNotNull(stops.destination))
            .map { Coordinates(it.latitude, it.longitude) }
    }
    // Wide or short windows show the map beside the details, at the full height, rather than
    // as a preview the details scroll away.
    val mapBeside = layout.prefersTwoPanes

    Row(modifier = modifier) {
        if (mapBeside) {
            MapRoutePreview(
                markers = markers,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = Spacing.screenHorizontal, top = Spacing.sm, bottom = Spacing.lg)
                    .clip(MaterialTheme.shapes.medium),
            )
        }
        CenteredContent(ContentWidth.list, modifier = Modifier.weight(1f).fillMaxHeight(), gutter = 0.dp) { margin ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = Spacing.lg).plusHorizontal(margin)
            ) {
                if (!mapBeside) {
                    item(key = "map") {
                        MapRoutePreview(
                            markers = markers,
                            modifier = Modifier
                                .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm)
                                .mediaPreviewSize()
                                .clip(MaterialTheme.shapes.medium),
                        )
                    }
                }

                if (draft != null) {
                    routeEditItems(draft = draft, isSaved = state.isSaved, onAction = onAction, onPickTime = onPickTime)
                } else {
                    routeReadItems(route = route, state = state)
                }

                state.error?.let { error ->
                    item(key = "error") {
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
}
