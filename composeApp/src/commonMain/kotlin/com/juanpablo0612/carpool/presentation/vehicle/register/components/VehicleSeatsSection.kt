package com.juanpablo0612.carpool.presentation.vehicle.register.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.presentation.ui.components.NumberStepper
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.vehicle_seats_helper
import enrutadoseia.composeapp.generated.resources.vehicle_seats_label
import org.jetbrains.compose.resources.stringResource

// 7. Seat count stepper
@Composable
internal fun VehicleSeatsSection(
    seatCount: Int,
    onSeatCountChanged: (Int) -> Unit
) {
    Text(
        text = stringResource(Res.string.vehicle_seats_label),
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(bottom = Spacing.xs)
    )
    NumberStepper(
        value = seatCount,
        onChange = { onSeatCountChanged(it) },
        min = 1,
        max = 7
    )
    Text(
        text = stringResource(Res.string.vehicle_seats_helper),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = Spacing.xs)
    )
    Spacer(Modifier.height(Spacing.lg))
}
