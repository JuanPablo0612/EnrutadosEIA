package com.juanpablo0612.carpool.presentation.place.stops

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_24px
import enrutadoseia.composeapp.generated.resources.add_waypoint_button
import enrutadoseia.composeapp.generated.resources.cd_stop_chosen_by_passenger
import enrutadoseia.composeapp.generated.resources.cd_stop_fixed
import enrutadoseia.composeapp.generated.resources.destination_label
import enrutadoseia.composeapp.generated.resources.origin_label
import enrutadoseia.composeapp.generated.resources.stop_number
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * Editable origin → waypoints → destination timeline, with add/remove stop. With
 * [endpointsLocked], origin and destination are shown locked and only the stops between them
 * can change. A waypoint whose name is in [lockedWaypointTags] is locked too, with its tag (who
 * chose it) underneath.
 */
fun LazyListScope.stopsEditorItems(
    stops: StopsDraft,
    onOriginClick: () -> Unit,
    onDestinationClick: () -> Unit,
    onEditWaypoint: (Int) -> Unit,
    onRemoveWaypoint: (Int) -> Unit,
    onAddWaypoint: () -> Unit,
    endpointsLocked: Boolean = false,
    lockedWaypointTags: Map<String, String> = emptyMap(),
) {
    item(key = "stop_origin") {
        RouteStopItem(
            label = stringResource(Res.string.origin_label),
            place = stops.origin,
            isLocked = endpointsLocked,
            onClick = onOriginClick,
            lockDescription = stringResource(Res.string.cd_stop_fixed),
        )
    }
    itemsIndexed(
        stops.waypoints,
        key = { index, waypoint -> "stop_waypoint_${waypoint.id.ifBlank { index.toString() }}_$index" },
    ) { index, waypoint ->
        val tag = lockedWaypointTags[waypoint.name]
        RouteStopItem(
            label = stringResource(Res.string.stop_number, index + 1),
            place = waypoint,
            isLocked = tag != null,
            onRemove = { onRemoveWaypoint(index) },
            onClick = { onEditWaypoint(index) },
            lockDescription = stringResource(Res.string.cd_stop_chosen_by_passenger),
            tag = tag,
        )
    }
    item(key = "stop_add") {
        TextButton(
            onClick = onAddWaypoint,
            // 40dp aligns the label under RouteStopItem's content column (24dp timeline + 16dp
            // spacer), not a spacing-scale value.
            modifier = Modifier.padding(horizontal = 40.dp),
        ) {
            Icon(vectorResource(Res.drawable.add_24px), contentDescription = null)
            Spacer(Modifier.width(Spacing.sm))
            Text(stringResource(Res.string.add_waypoint_button))
        }
    }
    item(key = "stop_destination") {
        RouteStopItem(
            label = stringResource(Res.string.destination_label),
            place = stops.destination,
            isLocked = endpointsLocked,
            showConnector = false,
            onClick = onDestinationClick,
            lockDescription = stringResource(Res.string.cd_stop_fixed),
        )
    }
}

/** The same timeline, read-only. */
fun LazyListScope.stopsReadOnlyItems(stops: StopsDraft) {
    item(key = "stop_origin") {
        RouteStopItem(
            label = stringResource(Res.string.origin_label),
            place = stops.origin,
            isLocked = true,
            onClick = {},
        )
    }
    itemsIndexed(
        stops.waypoints,
        key = { index, waypoint -> "stop_waypoint_${waypoint.id.ifBlank { index.toString() }}_$index" },
    ) { index, waypoint ->
        RouteStopItem(
            label = stringResource(Res.string.stop_number, index + 1),
            place = waypoint,
            isLocked = true,
            onClick = {},
        )
    }
    item(key = "stop_destination") {
        RouteStopItem(
            label = stringResource(Res.string.destination_label),
            place = stops.destination,
            isLocked = true,
            showConnector = false,
            onClick = {},
        )
    }
}
