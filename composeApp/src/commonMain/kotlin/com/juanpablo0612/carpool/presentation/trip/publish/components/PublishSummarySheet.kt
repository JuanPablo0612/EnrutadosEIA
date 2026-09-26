package com.juanpablo0612.carpool.presentation.trip.publish.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.publish_trip_confirm_button
import enrutadoseia.composeapp.generated.resources.publish_trip_summary_edit
import enrutadoseia.composeapp.generated.resources.publish_trip_summary_stops
import enrutadoseia.composeapp.generated.resources.publish_trip_summary_title
import enrutadoseia.composeapp.generated.resources.publish_trip_summary_will_save_route
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/** Last look at a trip before publishing it. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PublishSummarySheet(
    originName: String,
    destinationName: String,
    waypointCount: Int,
    whenText: String,
    vehicleText: String,
    message: String,
    routeNameToSave: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
                .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = stringResource(Res.string.publish_trip_summary_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.semantics { heading() },
            )
            Text(text = "$originName → $destinationName", style = MaterialTheme.typography.titleMedium)
            if (waypointCount > 0) {
                Text(
                    text = pluralStringResource(Res.plurals.publish_trip_summary_stops, waypointCount, waypointCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(text = whenText, style = MaterialTheme.typography.bodyLarge)
            Text(text = vehicleText, style = MaterialTheme.typography.bodyMedium)
            if (message.isNotBlank()) {
                Text(
                    text = "“${message.trim()}”",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            routeNameToSave?.let {
                Text(
                    text = stringResource(Res.string.publish_trip_summary_will_save_route, it),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text(stringResource(Res.string.publish_trip_summary_edit))
                }
                Button(onClick = onConfirm, modifier = Modifier.weight(1f)) {
                    Text(stringResource(Res.string.publish_trip_confirm_button))
                }
            }
        }
    }
}
