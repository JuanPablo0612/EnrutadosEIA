package com.juanpablo0612.carpool.presentation.trip.tracking.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.call_24px
import enrutadoseia.composeapp.generated.resources.my_location_24px
import enrutadoseia.composeapp.generated.resources.trip_tracking_sos_call_emergency
import enrutadoseia.composeapp.generated.resources.trip_tracking_sos_dismiss
import enrutadoseia.composeapp.generated.resources.trip_tracking_sos_location_shared
import enrutadoseia.composeapp.generated.resources.trip_tracking_sos_no_contacts
import enrutadoseia.composeapp.generated.resources.trip_tracking_sos_share_location
import enrutadoseia.composeapp.generated.resources.trip_tracking_sos_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun SosDialog(
    vibrateSosEnabled: Boolean,
    noContactsMessageVisible: Boolean,
    locationSharedMessageVisible: Boolean,
    onCallEmergency: () -> Unit,
    onShareLocation: () -> Unit,
    onDismiss: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(Unit) {
        if (vibrateSosEnabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.trip_tracking_sos_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                SosActionRow(
                    icon = vectorResource(Res.drawable.call_24px),
                    label = stringResource(Res.string.trip_tracking_sos_call_emergency),
                    onClick = onCallEmergency
                )
                SosActionRow(
                    icon = vectorResource(Res.drawable.my_location_24px),
                    label = stringResource(Res.string.trip_tracking_sos_share_location),
                    onClick = onShareLocation
                )
                if (noContactsMessageVisible) {
                    Text(
                        text = stringResource(Res.string.trip_tracking_sos_no_contacts),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.xs)
                    )
                }
                if (locationSharedMessageVisible) {
                    Text(
                        text = stringResource(Res.string.trip_tracking_sos_location_shared),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.xs)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.trip_tracking_sos_dismiss)) }
        }
    )
}
