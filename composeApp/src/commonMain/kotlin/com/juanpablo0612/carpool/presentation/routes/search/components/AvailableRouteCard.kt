package com.juanpablo0612.carpool.presentation.routes.search.components

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
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
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
import enrutadoseia.composeapp.generated.resources.route_from_university
import enrutadoseia.composeapp.generated.resources.route_to_university
import enrutadoseia.composeapp.generated.resources.route_waypoints_count
import enrutadoseia.composeapp.generated.resources.school_24px
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun AvailableRouteCard(
    route: Route,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SuggestionChip(
                    onClick = {},
                    label = {
                        Text(
                            text = if (route.type is RouteType.ToUniversity)
                                stringResource(Res.string.route_to_university)
                            else
                                stringResource(Res.string.route_from_university),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = vectorResource(Res.drawable.school_24px),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                Text(
                    text = route.targetTime.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = route.origin.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1
                    )
                    Text(
                        text = route.origin.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                Icon(
                    imageVector = vectorResource(Res.drawable.arrow_forward_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(20.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = route.destination.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1
                    )
                    Text(
                        text = route.destination.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            if (route.waypoints.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.route_waypoints_count, route.waypoints.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                route.daysOfWeek.sortedBy { it.ordinal }.forEach { day ->
                    DayChip(day = day)
                }
            }
        }
    }
}

@Composable
private fun DayChip(day: DayOfWeek) {
    val label = when (day) {
        DayOfWeek.MONDAY -> "Mo"
        DayOfWeek.TUESDAY -> "Tu"
        DayOfWeek.WEDNESDAY -> "We"
        DayOfWeek.THURSDAY -> "Th"
        DayOfWeek.FRIDAY -> "Fr"
        DayOfWeek.SATURDAY -> "Sa"
        DayOfWeek.SUNDAY -> "Su"
        else -> ""
    }
    SuggestionChip(
        onClick = {},
        label = { Text(text = label, style = MaterialTheme.typography.labelSmall) }
    )
}
