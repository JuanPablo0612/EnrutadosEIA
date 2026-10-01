package com.juanpablo0612.carpool.presentation.trip.publish.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.juanpablo0612.carpool.presentation.ui.components.ButtonPair
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.components.RouteTimeline
import com.juanpablo0612.carpool.presentation.ui.components.SecondaryButton
import com.juanpablo0612.carpool.presentation.ui.components.TimelineStop
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.contributionLabel
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.confirm_row_contribution
import enrutadoseia.composeapp.generated.resources.publish_summary_row_date
import enrutadoseia.composeapp.generated.resources.publish_summary_row_seats
import enrutadoseia.composeapp.generated.resources.publish_trip_confirm_button
import enrutadoseia.composeapp.generated.resources.publish_trip_summary_edit
import enrutadoseia.composeapp.generated.resources.publish_trip_summary_subtitle
import enrutadoseia.composeapp.generated.resources.publish_trip_summary_title
import enrutadoseia.composeapp.generated.resources.publish_trip_summary_will_save_route
import enrutadoseia.composeapp.generated.resources.select_vehicle_section
import enrutadoseia.composeapp.generated.resources.trip_detail_arrival
import enrutadoseia.composeapp.generated.resources.trip_detail_departure_caption
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PublishSummarySheet(
    summary: PublishSummary,
    isPublishing: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = stringResource(Res.string.publish_trip_summary_title),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = stringResource(Res.string.publish_trip_summary_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surfaceContainerLow) {
                RouteTimeline(
                    stops = summary.stopNames.mapIndexed { index, name ->
                        TimelineStop(
                            title = name,
                            caption = when (index) {
                                0 -> stringResource(Res.string.trip_detail_departure_caption, summary.timeText)
                                summary.stopNames.lastIndex -> stringResource(Res.string.trip_detail_arrival)
                                else -> null
                            },
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg),
                )
            }

            Column {
                SummaryRow(stringResource(Res.string.publish_summary_row_date), summary.dateText, isFirst = true)
                SummaryRow(stringResource(Res.string.publish_summary_row_seats), summary.seatCount.toString())
                SummaryRow(stringResource(Res.string.confirm_row_contribution), contributionLabel(summary.contributionPerPassenger))
                summary.vehicleText?.let { SummaryRow(stringResource(Res.string.select_vehicle_section), it) }
            }

            if (summary.message.isNotBlank()) {
                Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surfaceContainerLow) {
                    Text(
                        text = summary.message.trim(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg),
                    )
                }
            }

            summary.routeNameToSave?.let {
                Text(
                    text = stringResource(Res.string.publish_trip_summary_will_save_route, it),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            ButtonPair(
                secondary = {
                    SecondaryButton(
                        text = stringResource(Res.string.publish_trip_summary_edit),
                        onClick = onDismiss,
                        enabled = !isPublishing,
                        modifier = it,
                    )
                },
                primary = {
                    PrimaryButton(
                        text = stringResource(Res.string.publish_trip_confirm_button),
                        onClick = onConfirm,
                        isLoading = isPublishing,
                        modifier = it,
                    )
                },
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, isFirst: Boolean = false) {
    if (!isFirst) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.md),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
}
