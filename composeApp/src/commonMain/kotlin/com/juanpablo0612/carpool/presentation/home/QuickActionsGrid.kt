package com.juanpablo0612.carpool.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.auth.model.UserRole
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_24px
import enrutadoseia.composeapp.generated.resources.add_road_24px
import enrutadoseia.composeapp.generated.resources.bookmarks_24px
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.home_action_create_route
import enrutadoseia.composeapp.generated.resources.home_action_history
import enrutadoseia.composeapp.generated.resources.home_action_my_bookings
import enrutadoseia.composeapp.generated.resources.home_action_my_places
import enrutadoseia.composeapp.generated.resources.home_action_my_routes
import enrutadoseia.composeapp.generated.resources.home_action_my_trips
import enrutadoseia.composeapp.generated.resources.home_action_publish_trip
import enrutadoseia.composeapp.generated.resources.home_action_search_trip
import enrutadoseia.composeapp.generated.resources.home_create_route_cta
import enrutadoseia.composeapp.generated.resources.home_no_route_subtitle
import enrutadoseia.composeapp.generated.resources.home_no_route_title
import enrutadoseia.composeapp.generated.resources.home_no_vehicle_subtitle
import enrutadoseia.composeapp.generated.resources.home_no_vehicle_title
import enrutadoseia.composeapp.generated.resources.home_quick_actions_title
import enrutadoseia.composeapp.generated.resources.home_register_vehicle_cta
import enrutadoseia.composeapp.generated.resources.inbox_24px
import enrutadoseia.composeapp.generated.resources.location_on_24px
import enrutadoseia.composeapp.generated.resources.search_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun QuickActionsGrid(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.home_quick_actions_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(Spacing.sm))

        when (state.role) {
            UserRole.Driver -> DriverQuickActions(state = state, onAction = onAction)
            UserRole.Passenger -> PassengerQuickActions(onAction = onAction)
        }
    }
}

@Composable
private fun DriverQuickActions(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
) {
    when {
        !state.hasVehicles -> {
            OnboardingBanner(
                title = stringResource(Res.string.home_no_vehicle_title),
                subtitle = stringResource(Res.string.home_no_vehicle_subtitle),
                ctaLabel = stringResource(Res.string.home_register_vehicle_cta),
                onCta = { onAction(HomeAction.RegisterVehicle) },
            )
        }
        !state.hasRoutes -> {
            OnboardingBanner(
                title = stringResource(Res.string.home_no_route_title),
                subtitle = stringResource(Res.string.home_no_route_subtitle),
                ctaLabel = stringResource(Res.string.home_create_route_cta),
                onCta = { onAction(HomeAction.CreateRoute) },
            )
        }
        else -> {
            ActionGrid(
                items = listOf(
                    QuickActionItem(
                        icon = vectorResource(Res.drawable.add_road_24px),
                        label = stringResource(Res.string.home_action_publish_trip),
                        isPrimary = true,
                        onClick = { onAction(HomeAction.PublishTrip) },
                    ),
                    QuickActionItem(
                        icon = vectorResource(Res.drawable.add_24px),
                        label = stringResource(Res.string.home_action_create_route),
                        onClick = { onAction(HomeAction.CreateRoute) },
                    ),
                    QuickActionItem(
                        icon = vectorResource(Res.drawable.add_road_24px),
                        label = stringResource(Res.string.home_action_my_routes),
                        onClick = { onAction(HomeAction.ViewMyRoutes) },
                    ),
                    QuickActionItem(
                        icon = vectorResource(Res.drawable.directions_car_24px),
                        label = stringResource(Res.string.home_action_my_trips),
                        onClick = { onAction(HomeAction.ViewMyTrips) },
                    ),
                ),
            )
        }
    }
}

@Composable
private fun PassengerQuickActions(onAction: (HomeAction) -> Unit) {
    ActionGrid(
        items = listOf(
            QuickActionItem(
                icon = vectorResource(Res.drawable.search_24px),
                label = stringResource(Res.string.home_action_search_trip),
                isPrimary = true,
                onClick = { onAction(HomeAction.SearchTrips) },
            ),
            QuickActionItem(
                icon = vectorResource(Res.drawable.location_on_24px),
                label = stringResource(Res.string.home_action_my_places),
                onClick = { onAction(HomeAction.ViewSavedPlaces) },
            ),
            QuickActionItem(
                icon = vectorResource(Res.drawable.bookmarks_24px),
                label = stringResource(Res.string.home_action_my_bookings),
                onClick = { onAction(HomeAction.ViewMyBookings) },
            ),
            QuickActionItem(
                icon = vectorResource(Res.drawable.inbox_24px),
                label = stringResource(Res.string.home_action_history),
                onClick = { onAction(HomeAction.ViewMyBookings) },
            ),
        ),
    )
}

@Composable
private fun ActionGrid(items: List<QuickActionItem>) {
    val rows = items.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                row.forEach { item ->
                    QuickActionCard(
                        icon = item.icon,
                        label = item.label,
                        isPrimary = item.isPrimary,
                        onClick = item.onClick,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
) {
    val containerColor = if (isPrimary) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = if (isPrimary) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier
            .height(96.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
            )
        }
    }
}

@Composable
private fun OnboardingBanner(
    title: String,
    subtitle: String,
    ctaLabel: String,
    onCta: () -> Unit,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(Spacing.md))
            Button(onClick = onCta) {
                Text(ctaLabel)
            }
        }
    }
}

private data class QuickActionItem(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
    val isPrimary: Boolean = false,
)
