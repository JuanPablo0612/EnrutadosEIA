package com.juanpablo0612.carpool.presentation.trip.publish.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.presentation.ui.components.NumberStepper
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.trip_seats_helper
import enrutadoseia.composeapp.generated.resources.trip_seats_section
import org.jetbrains.compose.resources.stringResource

/**
 * Seats offered, from [minSeats] up to what the selected car holds; the cap, and any
 * [supportingText], are spelled out beside it. Pass [showCapacityHelper] false when the
 * supporting text already states the cap.
 */
@Composable
internal fun TripSeatsSection(
    seatCount: Int,
    selectedVehicle: Vehicle?,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minSeats: Int = 1,
    supportingText: String? = null,
    showCapacityHelper: Boolean = true,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            SectionLabel(text = stringResource(Res.string.trip_seats_section))
            selectedVehicle?.takeIf { showCapacityHelper }?.let { vehicle ->
                Text(
                    text = stringResource(Res.string.trip_seats_helper, "${vehicle.brand} ${vehicle.model}".trim(), vehicle.seatsAvailable),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            supportingText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        NumberStepper(
            value = seatCount,
            onChange = onChange,
            min = minSeats,
            max = selectedVehicle?.seatsAvailable ?: seatCount,
        )
    }
}
