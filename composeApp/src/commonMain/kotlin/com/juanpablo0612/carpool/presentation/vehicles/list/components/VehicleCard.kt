package com.juanpablo0612.carpool.presentation.vehicles.list.components

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.delete_24px
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.edit_24px
import enrutadoseia.composeapp.generated.resources.more_vert_24px
import enrutadoseia.composeapp.generated.resources.vehicle_action_delete
import enrutadoseia.composeapp.generated.resources.vehicle_action_edit
import enrutadoseia.composeapp.generated.resources.vehicle_action_set_primary
import enrutadoseia.composeapp.generated.resources.vehicle_primary_indicator
import enrutadoseia.composeapp.generated.resources.vehicle_verified
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import enrutadoseia.composeapp.generated.resources.vehicle_seats_count

@Composable
fun VehicleCard(
    vehicle: Vehicle,
    totalVehicleCount: Int,
    onEdit: () -> Unit,
    onSetPrimary: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val isVerified = vehicle.soatExpiresOn != null && vehicle.tecnomecanicaExpiresOn != null
            && vehicle.soatExpiresOn > today && vehicle.tecnomecanicaExpiresOn > today

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            VehiclePhoto(
                photoUrl = vehicle.photoUrl,
                modifier = Modifier
                    .size(width = 120.dp, height = 80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${vehicle.brand} ${vehicle.model} ${vehicle.year}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${vehicle.color} · ${vehicle.licensePlate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = pluralStringResource(
                        Res.plurals.vehicle_seats_count,
                        vehicle.seatsAvailable,
                        vehicle.seatsAvailable
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (vehicle.isPrimary && totalVehicleCount > 1) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "● ${stringResource(Res.string.vehicle_primary_indicator)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (isVerified) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "✓ ${stringResource(Res.string.vehicle_verified)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.more_vert_24px),
                        contentDescription = null
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.vehicle_action_edit)) },
                        leadingIcon = {
                            Icon(vectorResource(Res.drawable.edit_24px), contentDescription = null)
                        },
                        onClick = {
                            menuExpanded = false
                            onEdit()
                        }
                    )
                    if (!vehicle.isPrimary || totalVehicleCount > 1) {
                        if (!vehicle.isPrimary) {
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.vehicle_action_set_primary)) },
                                onClick = {
                                    menuExpanded = false
                                    onSetPrimary()
                                }
                            )
                        }
                    }
                    DropdownMenuItem(
                        text = {
                            Text(
                                stringResource(Res.string.vehicle_action_delete),
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        leadingIcon = {
                            Icon(
                                vectorResource(Res.drawable.delete_24px),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun VehiclePhoto(photoUrl: String, modifier: Modifier = Modifier) {
    if (photoUrl.isNotBlank()) {
        AsyncImage(
            model = photoUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = vectorResource(Res.drawable.directions_car_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
