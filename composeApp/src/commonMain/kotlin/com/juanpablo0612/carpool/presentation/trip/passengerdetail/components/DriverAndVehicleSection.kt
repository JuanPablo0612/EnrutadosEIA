package com.juanpablo0612.carpool.presentation.trip.passengerdetail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.auth.model.PublicProfile
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.presentation.ui.components.UserAvatar
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.trip_driver_placeholder
import enrutadoseia.composeapp.generated.resources.trip_driver_section
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun DriverAndVehicleSection(
    driver: PublicProfile?,
    vehicle: Vehicle?,
    modifier: Modifier = Modifier
) {
    val driverName = driver?.name?.takeIf { it.isNotBlank() }
        ?: stringResource(Res.string.trip_driver_placeholder)
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.trip_driver_section),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserAvatar(name = driverName, photoUrl = driver?.photoUrl, size = 36.dp) // avatar-intrinsic size
            Spacer(modifier = Modifier.size(Spacing.sm))
            Column {
                Text(
                    text = driverName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                vehicle?.let { v ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.directions_car_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp) // icon-intrinsic size
                        )
                        Spacer(modifier = Modifier.size(Spacing.xs))
                        Text(
                            text = "${v.brand} ${v.model} · ${v.color} · ${v.year} · ${v.licensePlate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
