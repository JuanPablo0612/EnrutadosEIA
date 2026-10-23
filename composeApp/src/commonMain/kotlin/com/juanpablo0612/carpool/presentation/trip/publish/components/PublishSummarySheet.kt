package com.juanpablo0612.carpool.presentation.trip.publish.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.contributionLabel
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.confirm_row_contribution
import enrutadoseia.composeapp.generated.resources.publish_summary_row_date
import enrutadoseia.composeapp.generated.resources.publish_summary_row_seats
import enrutadoseia.composeapp.generated.resources.publish_trip_confirm_button
import enrutadoseia.composeapp.generated.resources.publish_trip_summary_subtitle
import enrutadoseia.composeapp.generated.resources.publish_trip_summary_title
import enrutadoseia.composeapp.generated.resources.publish_trip_summary_will_save_route
import enrutadoseia.composeapp.generated.resources.select_vehicle_section
import org.jetbrains.compose.resources.stringResource

/** What a trip will look like once published, gathered for one last look. */
internal data class PublishSummary(
    val stopNames: List<String>,
    val dateText: String,
    val timeText: String,
    val seatCount: Int,
    val contributionPerPassenger: Int?,
    val vehicleText: String?,
    val message: String,
    /** The frequent route this publish will also save, if any. */
    val routeNameToSave: String?,
)

/**
 * The review step before publishing: the route as a timeline, the key facts as rows, the note as
 * passengers will read it, and whether a frequent route is saved alongside. "Editar" returns to
 * the form with everything kept.
 */
@Composable
internal fun PublishSummarySheet(
    summary: PublishSummary,
    isPublishing: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    ReviewSheet(
        title = stringResource(Res.string.publish_trip_summary_title),
        subtitle = stringResource(Res.string.publish_trip_summary_subtitle),
        confirmText = stringResource(Res.string.publish_trip_confirm_button),
        isPublishing = isPublishing,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    ) {
        RouteSummaryCard(stopNames = summary.stopNames, departureTimeText = summary.timeText)

        Column {
            SummaryRow(stringResource(Res.string.publish_summary_row_date), summary.dateText, isFirst = true)
            SummaryRow(stringResource(Res.string.publish_summary_row_seats), summary.seatCount.toString())
            SummaryRow(stringResource(Res.string.confirm_row_contribution), contributionLabel(summary.contributionPerPassenger))
            summary.vehicleText?.let { SummaryRow(stringResource(Res.string.select_vehicle_section), it) }
        }

        if (summary.message.isNotBlank()) {
            MessageSummaryCard(summary.message)
        }

        summary.routeNameToSave?.let {
            Text(
                text = stringResource(Res.string.publish_trip_summary_will_save_route, it),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

/** The note to passengers, as they will read it. */
@Composable
internal fun MessageSummaryCard(message: String) {
    Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surfaceContainerLow) {
        Text(
            text = message.trim(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
        )
    }
}
