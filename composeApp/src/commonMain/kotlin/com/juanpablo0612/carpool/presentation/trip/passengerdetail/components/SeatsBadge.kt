package com.juanpablo0612.carpool.presentation.trip.passengerdetail.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.available_seats_label
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SeatsBadge(availableSeats: Int, modifier: Modifier = Modifier) {
    val (bg, fg) = if (availableSeats > 0)
        MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    Surface(shape = MaterialTheme.shapes.extraSmall, color = bg, modifier = modifier) {
        Text(
            text = stringResource(Res.string.available_seats_label, availableSeats),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = fg,
            // vertical inset is a fine visual tweak below the 4dp scale step, kept as a literal
            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 3.dp)
        )
    }
}
