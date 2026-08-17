package com.juanpablo0612.carpool.presentation.routes.list.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.routes.create.components.TrajectoryConnector
import com.juanpablo0612.carpool.presentation.routes.list.RouteWithStats
import com.juanpablo0612.carpool.presentation.routes.orderedDays
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.route_last_used_separator
import enrutadoseia.composeapp.generated.resources.route_trips_count
import enrutadoseia.composeapp.generated.resources.add_road_24px
import enrutadoseia.composeapp.generated.resources.departure_time_label
import enrutadoseia.composeapp.generated.resources.cd_more_options
import enrutadoseia.composeapp.generated.resources.more_vert_24px
import enrutadoseia.composeapp.generated.resources.route_menu_delete
import enrutadoseia.composeapp.generated.resources.route_menu_duplicate
import enrutadoseia.composeapp.generated.resources.route_menu_edit
import enrutadoseia.composeapp.generated.resources.route_menu_publish_trip
import enrutadoseia.composeapp.generated.resources.route_waypoints_count
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun RouteCard(
    routeWithStats: RouteWithStats,
    onClick: () -> Unit,
    onPublishTripClick: () -> Unit,
    onEditClick: () -> Unit,
    onDuplicateClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val route = routeWithStats.route
    var expanded by remember { mutableStateOf(false) }

    CarpoolListCard(
        modifier = modifier,
        onClick = onClick,
    ) {
            // Header row: name + overflow menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = route.name.ifBlank { "${route.origin.name} → ${route.destination.name}" },
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                Box {
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier.size(32.dp) // compact touch target, not a spacing value
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.more_vert_24px),
                            contentDescription = stringResource(Res.string.cd_more_options),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.route_menu_publish_trip)) },
                            onClick = { expanded = false; onPublishTripClick() }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.route_menu_edit)) },
                            onClick = { expanded = false; onEditClick() }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.route_menu_duplicate)) },
                            onClick = { expanded = false; onDuplicateClick() }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(Res.string.route_menu_delete),
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = { expanded = false; onDeleteClick() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Origin block
            StopBlock(name = route.origin.name, address = route.origin.address)

            // Connector + waypoints indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                TrajectoryConnector(
                    // component-intrinsic timeline rail size, not a spacing value
                    modifier = Modifier
                        .width(24.dp)
                        .height(28.dp)
                )
                if (route.waypoints.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(
                        text = stringResource(Res.string.route_waypoints_count, route.waypoints.size),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Destination block
            StopBlock(name = route.destination.name, address = route.destination.address)

            // Recurrence row
            if (route.recurringDays.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    orderedDays.forEach { (day, res) ->
                        FilterChip(
                            selected = day in route.recurringDays,
                            onClick = {},
                            enabled = false,
                            label = {
                                Text(
                                    text = stringResource(res),
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                    route.typicalDepartureTime?.let { time ->
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text(
                            text = stringResource(
                                Res.string.departure_time_label,
                                "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Stats row
            if (routeWithStats.tripsCount > 0) {
                Spacer(modifier = Modifier.height(6.dp)) // fine visual gap, below the 4dp scale step
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.add_road_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp) // icon-intrinsic size
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    // Was "$count viajes" built in Kotlin: hardcoded Spanish that rendered
                    // untranslated in English, and unpluralized.
                    val tripsText = pluralStringResource(
                        Res.plurals.route_trips_count,
                        routeWithStats.tripsCount,
                        routeWithStats.tripsCount,
                    )
                    val statsText = routeWithStats.lastUsedAt?.let { instant ->
                        val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                        stringResource(Res.string.route_last_used_separator, tripsText, local.date.toString())
                    } ?: tripsText
                    Text(
                        text = statsText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
    }
}

@Composable
private fun StopBlock(name: String, address: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = address,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
