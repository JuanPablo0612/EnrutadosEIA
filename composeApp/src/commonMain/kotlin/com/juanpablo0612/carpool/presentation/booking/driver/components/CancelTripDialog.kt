package com.juanpablo0612.carpool.presentation.booking.driver.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.booking.driver.TripCancelError
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.trip_cancel_confirm_button
import enrutadoseia.composeapp.generated.resources.trip_cancel_confirm_title
import enrutadoseia.composeapp.generated.resources.trip_cancel_dialog_back
import enrutadoseia.composeapp.generated.resources.trip_cancel_dialog_confirmed
import enrutadoseia.composeapp.generated.resources.trip_cancel_dialog_notify_both
import enrutadoseia.composeapp.generated.resources.trip_cancel_dialog_notify_none
import enrutadoseia.composeapp.generated.resources.trip_cancel_dialog_notify_one
import enrutadoseia.composeapp.generated.resources.trip_cancel_dialog_pending
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Asks the driver to confirm cancelling a trip, naming who will hear about it. It stays open
 * while the request runs so a failure can be shown, and retried, right where it was triggered.
 */
@Composable
internal fun CancelTripDialog(
    confirmedCount: Int,
    pendingCount: Int,
    isCancelling: Boolean,
    error: TripCancelError?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.trip_cancel_confirm_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                Text(cancelTripDescription(confirmedCount, pendingCount))
                error?.let { ErrorMessage(message = stringResource(it.asStringResource())) }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isCancelling,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                ),
            ) {
                if (isCancelling) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp), // fits the button's label height
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onError,
                    )
                } else {
                    Text(stringResource(Res.string.trip_cancel_confirm_button))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isCancelling) {
                Text(stringResource(Res.string.trip_cancel_dialog_back))
            }
        },
    )
}

@Composable
private fun cancelTripDescription(confirmedCount: Int, pendingCount: Int): String {
    val confirmed = pluralStringResource(Res.plurals.trip_cancel_dialog_confirmed, confirmedCount, confirmedCount)
    val pending = pluralStringResource(Res.plurals.trip_cancel_dialog_pending, pendingCount, pendingCount)
    return when {
        confirmedCount > 0 && pendingCount > 0 ->
            stringResource(Res.string.trip_cancel_dialog_notify_both, confirmed, pending)
        confirmedCount > 0 -> stringResource(Res.string.trip_cancel_dialog_notify_one, confirmed)
        pendingCount > 0 -> stringResource(Res.string.trip_cancel_dialog_notify_one, pending)
        else -> stringResource(Res.string.trip_cancel_dialog_notify_none)
    }
}

@Preview
@Composable
private fun CancelTripDialogPreview() {
    CarpoolTheme {
        CancelTripDialog(
            confirmedCount = 2,
            pendingCount = 1,
            isCancelling = false,
            error = TripCancelError.Failed,
            onConfirm = {},
            onDismiss = {},
        )
    }
}
