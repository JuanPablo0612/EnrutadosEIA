package com.juanpablo0612.carpool.presentation.trip.publish.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.action_change
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/** The car a trip runs in, when the driver has only one: shown, not chosen. */
@Composable
internal fun SingleVehicleCard(
    vehicle: Vehicle,
    onChangeClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    CarpoolListCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            VehicleIconTile()
            VehicleLabels(vehicle = vehicle, modifier = Modifier.weight(1f))
            if (onChangeClick != null) {
                TextButton(onClick = onChangeClick) { Text(stringResource(Res.string.action_change)) }
            }
        }
    }
}

@Composable
internal fun VehicleIconTile() {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.small),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.directions_car_24px),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

/** "Mazda 3" over "Gris · ABC123". The driver sees their own plate so they pick the right car. */
@Composable
internal fun VehicleLabels(vehicle: Vehicle, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "${vehicle.brand} ${vehicle.model}".trim(), style = MaterialTheme.typography.titleMedium)
        Text(
            text = listOf(vehicle.color, vehicle.licensePlate).filter { it.isNotBlank() }.joinToString(" · "),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
