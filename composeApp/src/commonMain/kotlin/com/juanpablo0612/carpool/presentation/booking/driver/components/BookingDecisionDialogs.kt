package com.juanpablo0612.carpool.presentation.booking.driver.components

import androidx.compose.runtime.Composable
import com.juanpablo0612.carpool.presentation.booking.driver.decision.BookingDecisionAction
import com.juanpablo0612.carpool.presentation.booking.driver.decision.BookingDecisionsState
import com.juanpablo0612.carpool.presentation.ui.components.ConfirmDialog
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.confirmed_cancel_dialog_body
import enrutadoseia.composeapp.generated.resources.confirmed_cancel_dialog_button
import enrutadoseia.composeapp.generated.resources.confirmed_cancel_dialog_title
import org.jetbrains.compose.resources.stringResource

/** The reject sheet and the cancel confirmation, for any screen that hosts [BookingDecisionsState]. */
@Composable
internal fun BookingDecisionDialogs(state: BookingDecisionsState, onDecision: (BookingDecisionAction) -> Unit) {
    state.rejection?.let { RejectBottomSheet(draft = it, onDecision = onDecision) }
    state.cancelling?.let { booking ->
        ConfirmDialog(
            title = stringResource(Res.string.confirmed_cancel_dialog_title, booking.passengerName.substringBefore(' ')),
            description = stringResource(Res.string.confirmed_cancel_dialog_body),
            confirmText = stringResource(Res.string.confirmed_cancel_dialog_button),
            onConfirm = { onDecision(BookingDecisionAction.ConfirmCancel) },
            onDismiss = { onDecision(BookingDecisionAction.DismissCancel) },
            isDestructive = true,
        )
    }
}
