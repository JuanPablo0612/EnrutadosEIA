package com.juanpablo0612.carpool.presentation.route.community.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.route.model.Route
import com.juanpablo0612.carpool.presentation.route.create.components.TrajectoryConnector
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.community_routes_reuse_button
import enrutadoseia.composeapp.generated.resources.route_waypoints_count
import org.jetbrains.compose.resources.stringResource

@Composable
fun CommunityRouteCard(
    route: Route,
    onReuseClick: () -> Unit,
    modifier: Modifier = Modifier,
    isReusing: Boolean = false,
) {
    CarpoolListCard(modifier = modifier) {
        Text(
            text = route.name.ifBlank { "${route.origin.name} → ${route.destination.name}" },
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        CommunityStopBlock(name = route.origin.name, address = route.origin.address)

        Row(verticalAlignment = Alignment.CenterVertically) {
            TrajectoryConnector(
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

        CommunityStopBlock(name = route.destination.name, address = route.destination.address)

        Spacer(modifier = Modifier.height(Spacing.sm))

        PrimaryButton(
            text = stringResource(Res.string.community_routes_reuse_button),
            onClick = onReuseClick,
            isLoading = isReusing,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun CommunityStopBlock(name: String, address: String) {
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
