package com.juanpablo0612.carpool.presentation.booking.driver.components

import androidx.compose.runtime.Composable
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.confirmed_cancel_dialog_body
import enrutadoseia.composeapp.generated.resources.confirmed_cancel_dialog_button
import enrutadoseia.composeapp.generated.resources.confirmed_cancel_dialog_title
import org.jetbrains.compose.resources.stringResource

/** Shared by BookingRequestsScreen and TripPassengersScreen — both confirm cancelling a
 *  confirmed booking with identical copy. */
@Composable
fun BookingCancelConfirmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    ConfirmDialog(
        title = stringResource(Res.string.confirmed_cancel_dialog_title),
        description = stringResource(Res.string.confirmed_cancel_dialog_body),
        confirmText = stringResource(Res.string.confirmed_cancel_dialog_button),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        isDestructive = true,
    )
}
