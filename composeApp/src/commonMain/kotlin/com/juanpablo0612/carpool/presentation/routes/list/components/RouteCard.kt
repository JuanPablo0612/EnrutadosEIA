package com.juanpablo0612.carpool.presentation.routes.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.routes.create.components.TrajectoryConnector
import com.juanpablo0612.carpool.presentation.routes.list.RouteWithStats
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_road_24px
import enrutadoseia.composeapp.generated.resources.day_abbr_fri
import enrutadoseia.composeapp.generated.resources.day_abbr_mon
import enrutadoseia.composeapp.generated.resources.day_abbr_sat
import enrutadoseia.composeapp.generated.resources.day_abbr_sun
import enrutadoseia.composeapp.generated.resources.day_abbr_thu
import enrutadoseia.composeapp.generated.resources.day_abbr_tue
import enrutadoseia.composeapp.generated.resources.day_abbr_wed
import enrutadoseia.composeapp.generated.resources.departure_time_label
import enrutadoseia.composeapp.generated.resources.more_vert_24px
import enrutadoseia.composeapp.generated.resources.route_menu_delete
import enrutadoseia.composeapp.generated.resources.route_menu_duplicate
import enrutadoseia.composeapp.generated.resources.route_menu_edit
import enrutadoseia.composeapp.generated.resources.route_menu_publish_trip
import enrutadoseia.composeapp.generated.resources.route_waypoints_count
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.StringResource
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

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.more_vert_24px),
                            contentDescription = null,
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

            Spacer(modifier = Modifier.height(8.dp))

            // Origin block
            StopBlock(name = route.origin.name, address = route.origin.address)

            // Connector + waypoints indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                TrajectoryConnector(
                    modifier = Modifier
                        .width(24.dp)
                        .height(28.dp)
                )
                if (route.waypoints.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
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
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
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
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )
                    }
                    route.typicalDepartureTime?.let { time ->
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(
                                Res.string.departure_time_label,
                                "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Stats row
            if (routeWithStats.tripsCount > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.add_road_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val lastUsedText = routeWithStats.lastUsedAt?.let { instant ->
                        val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                        " · ${local.date}"
                    } ?: ""
                    Text(
                        text = "${routeWithStats.tripsCount} viajes$lastUsedText",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun StopBlock(name: String, address: String) {
    Column {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1
        )
        Text(
            text = address,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

private val orderedDays: List<Pair<DayOfWeek, StringResource>> = listOf(
    DayOfWeek.MONDAY to Res.string.day_abbr_mon,
    DayOfWeek.TUESDAY to Res.string.day_abbr_tue,
    DayOfWeek.WEDNESDAY to Res.string.day_abbr_wed,
    DayOfWeek.THURSDAY to Res.string.day_abbr_thu,
    DayOfWeek.FRIDAY to Res.string.day_abbr_fri,
    DayOfWeek.SATURDAY to Res.string.day_abbr_sat,
    DayOfWeek.SUNDAY to Res.string.day_abbr_sun
)
