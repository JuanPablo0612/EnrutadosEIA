package com.juanpablo0612.carpool.presentation.trip.create.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.date_other
import enrutadoseia.composeapp.generated.resources.date_today
import enrutadoseia.composeapp.generated.resources.date_tomorrow
import enrutadoseia.composeapp.generated.resources.departure_time_section_label
import enrutadoseia.composeapp.generated.resources.trip_when_section
import org.jetbrains.compose.resources.stringResource

internal enum class DateChip { Today, Tomorrow, Other }

@Composable
internal fun TripWhenSection(
    dateChipState: DateChip,
    formattedDate: String,
    onSelectToday: () -> Unit,
    onSelectTomorrow: () -> Unit,
    onShowDatePicker: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SectionLabel(
            text = stringResource(Res.string.trip_when_section),
            modifier = Modifier.padding(start = Spacing.lg, end = Spacing.lg, top = Spacing.md, bottom = Spacing.sm)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            FilterChip(
                selected = dateChipState == DateChip.Today,
                onClick = onSelectToday,
                label = { Text(stringResource(Res.string.date_today)) }
            )
            FilterChip(
                selected = dateChipState == DateChip.Tomorrow,
                onClick = onSelectTomorrow,
                label = { Text(stringResource(Res.string.date_tomorrow)) }
            )
            FilterChip(
                selected = dateChipState == DateChip.Other,
                onClick = onShowDatePicker,
                label = { Text(stringResource(Res.string.date_other)) }
            )
        }
        Spacer(modifier = Modifier.height(Spacing.sm))
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = Spacing.lg)
        )
    }
}

/** Departure-time picker trigger — grouped with [TripWhenSection] but kept as a separate
 *  composable since date and time are independent LazyColumn items. */
@Composable
internal fun TripTimeSection(
    formattedTime: String,
    onShowTimePicker: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SectionLabel(
            text = stringResource(Res.string.departure_time_section_label),
            modifier = Modifier.padding(start = Spacing.lg, end = Spacing.lg, top = Spacing.lg, bottom = Spacing.sm)
        )
        OutlinedButton(
            onClick = onShowTimePicker,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
        ) {
            Text(text = formattedTime)
        }
    }
}
