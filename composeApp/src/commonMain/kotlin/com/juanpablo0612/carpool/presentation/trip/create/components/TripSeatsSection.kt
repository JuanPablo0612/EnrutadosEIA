package com.juanpablo0612.carpool.presentation.trip.create.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.presentation.ui.components.NumberStepper
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.trip_seats_helper
import enrutadoseia.composeapp.generated.resources.trip_seats_section
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TripSeatsSection(
    seatCount: Int,
    selectedVehicle: Vehicle?,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SectionLabel(
            text = stringResource(Res.string.trip_seats_section),
            modifier = Modifier.padding(start = Spacing.lg, end = Spacing.lg, top = Spacing.lg, bottom = Spacing.sm)
        )
        NumberStepper(
            value = seatCount,
            onChange = onChange,
            min = 1,
            max = selectedVehicle?.seatsAvailable ?: seatCount,
            modifier = Modifier.padding(horizontal = Spacing.lg)
        )
        selectedVehicle?.let { v ->
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = stringResource(
                    Res.string.trip_seats_helper,
                    "${v.brand} ${v.model}",
                    v.seatsAvailable
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = Spacing.lg)
            )
        }
    }
}
