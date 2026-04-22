package com.juanpablo0612.carpool.presentation.routes.list.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.domain.routes.model.RouteType
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.arrow_forward_24px
import enrutadoseia.composeapp.generated.resources.day_fri
import enrutadoseia.composeapp.generated.resources.day_mon
import enrutadoseia.composeapp.generated.resources.day_sat
import enrutadoseia.composeapp.generated.resources.day_sun
import enrutadoseia.composeapp.generated.resources.day_thu
import enrutadoseia.composeapp.generated.resources.day_tue
import enrutadoseia.composeapp.generated.resources.day_wed
import enrutadoseia.composeapp.generated.resources.route_from_university
import enrutadoseia.composeapp.generated.resources.route_to_university
import enrutadoseia.composeapp.generated.resources.route_waypoints_count
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun RouteCard(
    route: Route,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RouteTypeBadge(type = route.type)
                Text(
                    text = formatTime(route.targetTime.hour, route.targetTime.minute),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = route.origin.name,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1
                    )
                    if (route.waypoints.isNotEmpty()) {
                        Text(
                            text = stringResource(Res.string.route_waypoints_count, route.waypoints.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = vectorResource(Res.drawable.arrow_forward_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = route.destination.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                DayOfWeek.entries.forEach { day ->
                    val isSelected = day in route.daysOfWeek
                    DayChip(
                        label = dayAbbreviation(day),
                        isSelected = isSelected
                    )
                }
            }
        }
    }
}

@Composable
private fun RouteTypeBadge(type: RouteType) {
    val label = when (type) {
        is RouteType.ToUniversity -> stringResource(Res.string.route_to_university)
        is RouteType.FromUniversity -> stringResource(Res.string.route_from_university)
    }
    val containerColor = when (type) {
        is RouteType.ToUniversity -> MaterialTheme.colorScheme.primaryContainer
        is RouteType.FromUniversity -> MaterialTheme.colorScheme.secondaryContainer
    }
    val contentColor = when (type) {
        is RouteType.ToUniversity -> MaterialTheme.colorScheme.onPrimaryContainer
        is RouteType.FromUniversity -> MaterialTheme.colorScheme.onSecondaryContainer
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = containerColor
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = contentColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun DayChip(label: String, isSelected: Boolean) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun dayAbbreviation(day: DayOfWeek): String = when (day) {
    DayOfWeek.MONDAY -> stringResource(Res.string.day_mon)
    DayOfWeek.TUESDAY -> stringResource(Res.string.day_tue)
    DayOfWeek.WEDNESDAY -> stringResource(Res.string.day_wed)
    DayOfWeek.THURSDAY -> stringResource(Res.string.day_thu)
    DayOfWeek.FRIDAY -> stringResource(Res.string.day_fri)
    DayOfWeek.SATURDAY -> stringResource(Res.string.day_sat)
    DayOfWeek.SUNDAY -> stringResource(Res.string.day_sun)
    else -> day.name.take(3)
}

private fun formatTime(hour: Int, minute: Int): String =
    "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
