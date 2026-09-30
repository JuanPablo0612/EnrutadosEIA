package com.juanpablo0612.carpool.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.arrow_forward_24px
import enrutadoseia.composeapp.generated.resources.cd_hide_vehicle_suggestion
import enrutadoseia.composeapp.generated.resources.close_24px
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.home_how_it_works_1
import enrutadoseia.composeapp.generated.resources.home_how_it_works_2
import enrutadoseia.composeapp.generated.resources.home_how_it_works_3
import enrutadoseia.composeapp.generated.resources.home_how_it_works_title
import enrutadoseia.composeapp.generated.resources.home_start_driver_action
import enrutadoseia.composeapp.generated.resources.home_start_driver_body
import enrutadoseia.composeapp.generated.resources.home_start_driver_title
import enrutadoseia.composeapp.generated.resources.home_start_rider_action
import enrutadoseia.composeapp.generated.resources.home_start_rider_body
import enrutadoseia.composeapp.generated.resources.home_start_rider_title
import enrutadoseia.composeapp.generated.resources.home_start_subtitle
import enrutadoseia.composeapp.generated.resources.home_start_title
import enrutadoseia.composeapp.generated.resources.home_vehicle_suggestion_action
import enrutadoseia.composeapp.generated.resources.home_vehicle_suggestion_body
import enrutadoseia.composeapp.generated.resources.home_vehicle_suggestion_title
import enrutadoseia.composeapp.generated.resources.person_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * A new account's two ways in, side by side and equally weighted: riding and driving are both
 * optional, so neither is presented as a step the other depends on.
 */
@Composable
fun HowToStartSection(
    onFindSeat: () -> Unit,
    onRegisterVehicle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = stringResource(Res.string.home_start_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = stringResource(Res.string.home_start_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            StartOptionCard(
                icon = vectorResource(Res.drawable.person_24px),
                title = stringResource(Res.string.home_start_rider_title),
                body = stringResource(Res.string.home_start_rider_body),
                action = stringResource(Res.string.home_start_rider_action),
                onClick = onFindSeat,
                modifier = Modifier.weight(1f),
            )
            StartOptionCard(
                icon = vectorResource(Res.drawable.directions_car_24px),
                title = stringResource(Res.string.home_start_driver_title),
                body = stringResource(Res.string.home_start_driver_body),
                action = stringResource(Res.string.home_start_driver_action),
                onClick = onRegisterVehicle,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StartOptionCard(
    icon: ImageVector,
    title: String,
    body: String,
    action: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CarpoolListCard(modifier = modifier, onClick = onClick) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            // Three lines either way, so the two cards' actions line up side by side.
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                minLines = 3,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Text(
                    text = action,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Icon(
                    imageVector = vectorResource(Res.drawable.arrow_forward_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

/** A static primer on how a shared trip works, told from both sides; no data behind it. */
@Composable
fun HowItWorksSection(modifier: Modifier = Modifier) {
    val steps = listOf(
        stringResource(Res.string.home_how_it_works_1),
        stringResource(Res.string.home_how_it_works_2),
        stringResource(Res.string.home_how_it_works_3),
    )
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(
            text = stringResource(Res.string.home_how_it_works_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        CarpoolListCard(contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.xs)) {
            steps.forEachIndexed { index, step ->
                if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Row(
                    modifier = Modifier.padding(vertical = Spacing.md),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    Text(
                        text = (index + 1).toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.width(20.dp),
                    )
                    Text(text = step, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

/**
 * Tells a rider without a car that they could drive too. Deliberately quiet (neutral colors,
 * a text action) and hideable for good, since plenty of riders will never have a car.
 */
@Composable
fun VehicleSuggestionCard(
    onRegisterVehicle: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CarpoolListCard(
        modifier = modifier,
        contentPadding = PaddingValues(start = Spacing.lg, top = Spacing.md, bottom = Spacing.xs, end = Spacing.xs),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            Box(
                modifier = Modifier
                    .padding(top = Spacing.xs)
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.directions_car_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            }
            Column(modifier = Modifier.weight(1f).padding(top = Spacing.xs)) {
                Text(
                    text = stringResource(Res.string.home_vehicle_suggestion_title),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = stringResource(Res.string.home_vehicle_suggestion_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(
                    onClick = onRegisterVehicle,
                    contentPadding = PaddingValues(horizontal = 0.dp),
                ) {
                    Text(stringResource(Res.string.home_vehicle_suggestion_action))
                }
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = vectorResource(Res.drawable.close_24px),
                    contentDescription = stringResource(Res.string.cd_hide_vehicle_suggestion),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview
@Composable
private fun HowToStartSectionPreview() {
    CarpoolTheme {
        HowToStartSection(onFindSeat = {}, onRegisterVehicle = {})
    }
}

@Preview
@Composable
private fun VehicleSuggestionCardPreview() {
    CarpoolTheme {
        VehicleSuggestionCard(onRegisterVehicle = {}, onDismiss = {})
    }
}
