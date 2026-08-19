package com.juanpablo0612.carpool.presentation.trip.tracking.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.juanpablo0612.carpool.domain.trip.model.PickupStatus
import com.juanpablo0612.carpool.presentation.ui.theme.Alpha
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.pickup_status_dropped_off
import enrutadoseia.composeapp.generated.resources.pickup_status_picked_up
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PickupStatusChip(status: PickupStatus) {
    val (label, color) = when (status) {
        is PickupStatus.Waiting -> "" to MaterialTheme.colorScheme.outlineVariant
        is PickupStatus.PickedUp -> stringResource(Res.string.pickup_status_picked_up) to MaterialTheme.colorScheme.primary
        is PickupStatus.DroppedOff -> stringResource(Res.string.pickup_status_dropped_off) to MaterialTheme.colorScheme.secondary
    }
    if (label.isNotBlank()) {
        SuggestionChip(
            onClick = {},
            label = { Text(label) },
            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = color.copy(alpha = Alpha.BADGE_CONTAINER))
        )
    }
}
