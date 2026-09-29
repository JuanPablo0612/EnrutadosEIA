package com.juanpablo0612.carpool.presentation.trip.publish.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.action_change
import enrutadoseia.composeapp.generated.resources.calendar_month_24px
import enrutadoseia.composeapp.generated.resources.date_other
import enrutadoseia.composeapp.generated.resources.date_today
import enrutadoseia.composeapp.generated.resources.date_tomorrow
import enrutadoseia.composeapp.generated.resources.departure_time_not_set
import enrutadoseia.composeapp.generated.resources.schedule_24px
import enrutadoseia.composeapp.generated.resources.trip_detail_departs_at
import enrutadoseia.composeapp.generated.resources.trip_when_section
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

internal sealed class DateChip {
    data object Today : DateChip()
    data object Tomorrow : DateChip()
    data object Other : DateChip()
}

/**
 * When the trip leaves: one-tap chips for today and tomorrow (most trips), "Otro día" for the
 * calendar, the chosen date spelled out, and the departure time as a tappable row.
 */
@Composable
internal fun TripWhenSection(
    dateChipState: DateChip,
    formattedDate: String,
    formattedTime: String,
    onSelectToday: () -> Unit,
    onSelectTomorrow: () -> Unit,
    onShowDatePicker: () -> Unit,
    onShowTimePicker: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        SectionLabel(text = stringResource(Res.string.trip_when_section))
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            FilterChip(
                selected = dateChipState == DateChip.Today,
                onClick = onSelectToday,
                label = { Text(stringResource(Res.string.date_today)) },
            )
            FilterChip(
                selected = dateChipState == DateChip.Tomorrow,
                onClick = onSelectTomorrow,
                label = { Text(stringResource(Res.string.date_tomorrow)) },
            )
            FilterChip(
                selected = dateChipState == DateChip.Other,
                onClick = onShowDatePicker,
                label = { Text(stringResource(Res.string.date_other)) },
                leadingIcon = {
                    Icon(
                        imageVector = vectorResource(Res.drawable.calendar_month_24px),
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                    )
                },
            )
        }
        if (formattedDate.isNotBlank()) {
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        CarpoolListCard(onClick = onShowTimePicker) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.schedule_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = if (formattedTime.isBlank()) {
                        stringResource(Res.string.departure_time_not_set)
                    } else {
                        stringResource(Res.string.trip_detail_departs_at, formattedTime)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(Res.string.action_change),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = Spacing.sm),
                )
            }
        }
    }
}
