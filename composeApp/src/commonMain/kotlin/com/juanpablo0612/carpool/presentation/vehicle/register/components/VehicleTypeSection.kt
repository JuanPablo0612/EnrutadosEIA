package com.juanpablo0612.carpool.presentation.vehicle.register.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.domain.vehicle.model.VehicleType
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.vehicle_type_hatchback
import enrutadoseia.composeapp.generated.resources.vehicle_type_label
import enrutadoseia.composeapp.generated.resources.vehicle_type_other
import enrutadoseia.composeapp.generated.resources.vehicle_type_pickup
import enrutadoseia.composeapp.generated.resources.vehicle_type_sedan
import enrutadoseia.composeapp.generated.resources.vehicle_type_suv
import org.jetbrains.compose.resources.stringResource

// 8. Vehicle type (optional)
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun VehicleTypeSection(
    type: VehicleType?,
    onTypeSelected: (VehicleType?) -> Unit
) {
    Text(
        text = stringResource(Res.string.vehicle_type_label),
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(bottom = Spacing.sm)
    )
    val typeEntries = listOf(
        VehicleType.Sedan to stringResource(Res.string.vehicle_type_sedan),
        VehicleType.Hatchback to stringResource(Res.string.vehicle_type_hatchback),
        VehicleType.SUV to stringResource(Res.string.vehicle_type_suv),
        VehicleType.Pickup to stringResource(Res.string.vehicle_type_pickup),
        VehicleType.Other to stringResource(Res.string.vehicle_type_other),
    )
    FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        typeEntries.forEach { (entryType, label) ->
            FilterChip(
                selected = type == entryType,
                onClick = { onTypeSelected(entryType) },
                label = { Text(label) }
            )
        }
    }
    Spacer(Modifier.height(Spacing.lg))
}
