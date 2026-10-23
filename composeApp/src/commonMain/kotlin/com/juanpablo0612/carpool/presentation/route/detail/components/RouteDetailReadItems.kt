package com.juanpablo0612.carpool.presentation.route.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.presentation.place.stops.StopsDraft
import com.juanpablo0612.carpool.presentation.place.stops.stopsReadOnlyItems
import com.juanpablo0612.carpool.presentation.route.create.components.SectionHeader
import com.juanpablo0612.carpool.presentation.route.detail.RouteDetailUiState
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.formatNumericDate
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.route_detail_last_used
import enrutadoseia.composeapp.generated.resources.route_detail_never_used
import enrutadoseia.composeapp.generated.resources.route_detail_stats_section
import enrutadoseia.composeapp.generated.resources.route_detail_trajectory_section
import enrutadoseia.composeapp.generated.resources.route_detail_trips_published
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

/** The saved route as it is: its schedule, its stops and how often it has been used. */
internal fun LazyListScope.routeReadItems(route: Route, state: RouteDetailUiState) {
    if (route.recurringDays.isNotEmpty()) {
        item(key = "recurrence") {
            RecurrenceRow(
                recurringDays = route.recurringDays,
                typicalDepartureTime = route.typicalDepartureTime
            )
        }
    }

    item(key = "trajectory_header") { SectionHeader(stringResource(Res.string.route_detail_trajectory_section)) }

    stopsReadOnlyItems(StopsDraft.of(route))

    item(key = "stats_header") { SectionHeader(stringResource(Res.string.route_detail_stats_section)) }

    item(key = "stats") {
        Column(modifier = Modifier.padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.xs)) {
            if (state.tripsPublished > 0) {
                Text(
                    text = stringResource(Res.string.route_detail_trips_published, state.tripsPublished),
                    style = MaterialTheme.typography.bodyMedium
                )
                state.lastUsedAt?.let { instant ->
                    val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                    Text(
                        text = stringResource(Res.string.route_detail_last_used, formatNumericDate(local.date)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    text = stringResource(Res.string.route_detail_never_used),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
