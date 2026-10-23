package com.juanpablo0612.carpool.presentation.trip.publish.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import com.juanpablo0612.carpool.presentation.ui.components.ButtonPair
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.components.RouteTimeline
import com.juanpablo0612.carpool.presentation.ui.components.SecondaryButton
import com.juanpablo0612.carpool.presentation.ui.components.TimelineStop
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.publish_trip_summary_edit
import enrutadoseia.composeapp.generated.resources.trip_detail_arrival
import enrutadoseia.composeapp.generated.resources.trip_detail_departure_caption
import org.jetbrains.compose.resources.stringResource

/**
 * The bottom sheet shared by the publish review steps: a heading, the [content] to check, and
 * "Editar" / [confirmText] buttons. While [isPublishing] the sheet can't be swiped away, so the
 * driver sees the outcome.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReviewSheet(
    title: String,
    subtitle: String,
    confirmText: String,
    isPublishing: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val publishing by rememberUpdatedState(isPublishing)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { it != SheetValue.Hidden || !publishing },
        ),
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
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            content()

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
                        text = confirmText,
                        onClick = onConfirm,
                        isLoading = isPublishing,
                        modifier = it,
                    )
                },
            )
        }
    }
}

/** The stops as a timeline, with the departure time on the first one. */
@Composable
internal fun RouteSummaryCard(stopNames: List<String>, departureTimeText: String?) {
    Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surfaceContainerLow) {
        RouteTimeline(
            stops = stopNames.mapIndexed { index, name ->
                TimelineStop(
                    title = name,
                    caption = when (index) {
                        0 -> departureTimeText?.let { stringResource(Res.string.trip_detail_departure_caption, it) }
                        stopNames.lastIndex -> stringResource(Res.string.trip_detail_arrival)
                        else -> null
                    },
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
        )
    }
}

/** A label and its value on one line, divided from the row above unless it [isFirst]. */
@Composable
internal fun SummaryRow(label: String, value: String, isFirst: Boolean = false) {
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

/** "Mazda 3 · ABC123", leaving out whatever is blank. */
internal fun Vehicle.summaryText(): String =
    listOf("$brand $model".trim(), licensePlate).filter(String::isNotBlank).joinToString(" · ")
