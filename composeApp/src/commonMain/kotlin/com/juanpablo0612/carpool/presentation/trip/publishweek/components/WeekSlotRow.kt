package com.juanpablo0612.carpool.presentation.trip.publishweek.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.trip.model.RecurringTripSlot
import com.juanpablo0612.carpool.domain.trip.model.SlotStatus
import com.juanpablo0612.carpool.presentation.ui.theme.LocalExtendedColors
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.label_pair
import enrutadoseia.composeapp.generated.resources.publish_week_slot_already_published
import enrutadoseia.composeapp.generated.resources.publish_week_slot_conflict
import enrutadoseia.composeapp.generated.resources.publish_week_slot_passed
import org.jetbrains.compose.resources.stringResource

/**
 * One day of a recurring route: a checkbox row when it can be published, a dimmed row saying why
 * when it can't.
 */
@Composable
internal fun WeekSlotRow(
    slot: RecurringTripSlot,
    dayLabel: String,
    timeLabel: String,
    conflictTimeLabel: String?,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val publishable = slot.status.isPublishable
    val statusText = when (slot.status) {
        is SlotStatus.AlreadyPublished -> stringResource(Res.string.publish_week_slot_already_published)
        SlotStatus.Passed -> stringResource(Res.string.publish_week_slot_passed)
        is SlotStatus.Conflict -> stringResource(Res.string.publish_week_slot_conflict, conflictTimeLabel.orEmpty())
        SlotStatus.Available -> null
    }
    val rowModifier = if (publishable) {
        Modifier.toggleable(value = isSelected, role = Role.Checkbox, onValueChange = { onToggle() })
    } else {
        // Announce why the day is off instead of just "disabled".
        Modifier.semantics { statusText?.let { stateDescription = it } }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(rowModifier)
            .heightIn(min = 56.dp)
            .padding(horizontal = Spacing.xs, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = publishable && isSelected, onCheckedChange = null, enabled = publishable)
        Column(modifier = Modifier.padding(start = Spacing.sm)) {
            Text(
                text = stringResource(Res.string.label_pair, dayLabel, timeLabel),
                style = MaterialTheme.typography.bodyLarge,
                color = if (publishable) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            statusText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    // A clash is a heads-up to the driver, not a failure: warning, not error.
                    color = when (slot.status) {
                        is SlotStatus.Conflict -> LocalExtendedColors.current.warning
                        is SlotStatus.AlreadyPublished -> LocalExtendedColors.current.success
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }
}
