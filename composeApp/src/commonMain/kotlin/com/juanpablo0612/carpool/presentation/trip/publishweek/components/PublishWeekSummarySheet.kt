package com.juanpablo0612.carpool.presentation.trip.publishweek.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.juanpablo0612.carpool.domain.trip.model.SlotStatus
import com.juanpablo0612.carpool.presentation.trip.publish.components.MessageSummaryCard
import com.juanpablo0612.carpool.presentation.trip.publish.components.ReviewSheet
import com.juanpablo0612.carpool.presentation.trip.publish.components.RouteSummaryCard
import com.juanpablo0612.carpool.presentation.trip.publish.components.SectionLabel
import com.juanpablo0612.carpool.presentation.trip.publish.components.SummaryRow
import com.juanpablo0612.carpool.presentation.trip.publish.components.summaryText
import com.juanpablo0612.carpool.presentation.trip.publishweek.PublishWeekSummary
import com.juanpablo0612.carpool.presentation.trip.publishweek.SkipReason
import com.juanpablo0612.carpool.presentation.ui.theme.LocalExtendedColors
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.contributionLabel
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.confirm_row_contribution
import enrutadoseia.composeapp.generated.resources.label_pair
import enrutadoseia.composeapp.generated.resources.publish_summary_row_seats
import enrutadoseia.composeapp.generated.resources.publish_week_publish_button
import enrutadoseia.composeapp.generated.resources.publish_week_slot_already_published
import enrutadoseia.composeapp.generated.resources.publish_week_slot_conflict
import enrutadoseia.composeapp.generated.resources.publish_week_slot_passed
import enrutadoseia.composeapp.generated.resources.publish_week_summary_skip_not_selected
import enrutadoseia.composeapp.generated.resources.publish_week_summary_skipped_label
import enrutadoseia.composeapp.generated.resources.publish_week_summary_subtitle
import enrutadoseia.composeapp.generated.resources.publish_week_summary_title
import enrutadoseia.composeapp.generated.resources.publish_week_summary_trips_label
import enrutadoseia.composeapp.generated.resources.select_vehicle_section
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant

/**
 * The review step before publishing a week: the route, every day that gets a trip (flagging the
 * ones close to another trip), the settings they share, and the days left out with the reason.
 */
@Composable
internal fun PublishWeekSummarySheet(
    summary: PublishWeekSummary,
    isPublishing: Boolean,
    dayLabel: (LocalDate) -> String,
    timeLabel: (Instant) -> String,
    routeTimeText: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val route = summary.route
    ReviewSheet(
        title = stringResource(Res.string.publish_week_summary_title),
        subtitle = stringResource(Res.string.publish_week_summary_subtitle),
        confirmText = pluralStringResource(Res.plurals.publish_week_publish_button, summary.trips.size, summary.trips.size),
        isPublishing = isPublishing,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    ) {
        RouteSummaryCard(
            stopNames = listOf(route.origin.name) + route.waypoints.map { it.name } + route.destination.name,
            departureTimeText = routeTimeText,
        )

        SlotGroup(label = stringResource(Res.string.publish_week_summary_trips_label)) {
            summary.trips.forEachIndexed { index, slot ->
                val conflict = slot.status as? SlotStatus.Conflict
                SlotLine(
                    title = stringResource(Res.string.label_pair, dayLabel(slot.date), timeLabel(slot.departure)),
                    note = conflict?.let { stringResource(Res.string.publish_week_slot_conflict, timeLabel(it.departure)) },
                    noteColor = LocalExtendedColors.current.warning,
                    isFirst = index == 0,
                )
            }
        }

        Column {
            SummaryRow(stringResource(Res.string.publish_summary_row_seats), summary.seatCount.toString(), isFirst = true)
            SummaryRow(stringResource(Res.string.confirm_row_contribution), contributionLabel(summary.contributionPerPassenger))
            summary.vehicle?.let { SummaryRow(stringResource(Res.string.select_vehicle_section), it.summaryText()) }
        }

        if (summary.message.isNotBlank()) {
            MessageSummaryCard(summary.message)
        }

        if (summary.skipped.isNotEmpty()) {
            SlotGroup(label = stringResource(Res.string.publish_week_summary_skipped_label)) {
                summary.skipped.forEachIndexed { index, skipped ->
                    val notSelected = stringResource(Res.string.publish_week_summary_skip_not_selected)
                    val reason = when (val r = skipped.reason) {
                        SkipReason.NotSelected -> notSelected
                        is SkipReason.Conflict -> stringResource(
                            Res.string.label_pair,
                            stringResource(Res.string.publish_week_slot_conflict, timeLabel(r.otherDeparture)),
                            notSelected,
                        )
                        SkipReason.AlreadyPublished -> stringResource(Res.string.publish_week_slot_already_published)
                        SkipReason.Passed -> stringResource(Res.string.publish_week_slot_passed)
                    }
                    SlotLine(
                        title = stringResource(Res.string.label_pair, dayLabel(skipped.slot.date), timeLabel(skipped.slot.departure)),
                        note = reason,
                        noteColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        isFirst = index == 0,
                        isDimmed = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun SlotGroup(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        SectionLabel(text = label)
        Column { content() }
    }
}

/** One day: its date and time, and an optional note under it. */
@Composable
private fun SlotLine(title: String, note: String?, noteColor: Color, isFirst: Boolean, isDimmed: Boolean = false) {
    if (!isFirst) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isDimmed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
        )
        note?.let {
            Text(text = it, style = MaterialTheme.typography.bodySmall, color = noteColor)
        }
    }
}
