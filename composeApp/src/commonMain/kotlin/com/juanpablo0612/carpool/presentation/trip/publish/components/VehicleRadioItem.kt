package com.juanpablo0612.carpool.presentation.trip.publish.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing

/**
 * One of several cars to choose from. The whole card is the radio target; the selected one gets
 * a primary border so the choice reads at a glance, not only through the small radio dot.
 */
@Composable
internal fun VehicleRadioItem(
    vehicle: Vehicle,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CarpoolListCard(
        modifier = modifier
            .widthIn(min = 220.dp)
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
        contentPadding = PaddingValues(start = Spacing.xs, end = Spacing.lg, top = Spacing.sm, bottom = Spacing.sm),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            // onClick = null: the card owns the selection, so the dot isn't a second target.
            RadioButton(selected = isSelected, onClick = null, modifier = Modifier.padding(Spacing.md))
            VehicleLabels(vehicle = vehicle)
        }
    }
}
