package com.juanpablo0612.carpool.presentation.vehicle.list.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.delete_24px
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.edit_24px
import enrutadoseia.composeapp.generated.resources.cd_more_options
import enrutadoseia.composeapp.generated.resources.more_vert_24px
import enrutadoseia.composeapp.generated.resources.vehicle_action_delete
import enrutadoseia.composeapp.generated.resources.vehicle_action_edit
import enrutadoseia.composeapp.generated.resources.vehicle_action_set_primary
import enrutadoseia.composeapp.generated.resources.vehicle_primary_indicator
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
    // Nullable: the vehicles list has no per-vehicle detail destination to navigate to. Screens
    // that gain one later can pass a lambda; until then the call site passes null and the row
    // stays a non-clickable list item (its overflow menu is the only affordance).
    onClick: (() -> Unit)? = null,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    CarpoolListCard(modifier = modifier, onClick = onClick) {
        Row(verticalAlignment = Alignment.Top) {
            VehiclePhoto(
                photoUrl = vehicle.photoUrl,
                modifier = Modifier
                    .size(width = 120.dp, height = 80.dp) // component-intrinsic photo thumbnail size
                    .clip(MaterialTheme.shapes.small)
            )
            Spacer(modifier = Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${vehicle.brand} ${vehicle.model} ${vehicle.year}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(2.dp)) // below the 4dp spacing floor; a hairline-level text gap
                Text(
                    text = "${vehicle.color} · ${vehicle.licensePlate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
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
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = "● ${stringResource(Res.string.vehicle_primary_indicator)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.more_vert_24px),
                        contentDescription = stringResource(Res.string.cd_more_options)
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
            shape = MaterialTheme.shapes.small
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = vectorResource(Res.drawable.directions_car_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp) // component-intrinsic icon size
                )
            }
        }
    }
}
