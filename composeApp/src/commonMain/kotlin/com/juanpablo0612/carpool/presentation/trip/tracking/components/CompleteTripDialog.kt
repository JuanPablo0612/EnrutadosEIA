package com.juanpablo0612.carpool.presentation.trip.tracking.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.cancel_button
import enrutadoseia.composeapp.generated.resources.trip_tracking_complete_confirm_body
import enrutadoseia.composeapp.generated.resources.trip_tracking_complete_confirm_button
import enrutadoseia.composeapp.generated.resources.trip_tracking_complete_confirm_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CompleteTripDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.trip_tracking_complete_confirm_title)) },
        text = { Text(stringResource(Res.string.trip_tracking_complete_confirm_body)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(Res.string.trip_tracking_complete_confirm_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel_button))
            }
        }
    )
}
