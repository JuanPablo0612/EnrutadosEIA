package com.juanpablo0612.carpool.presentation.booking.driver.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.booking.model.RejectReason
import com.juanpablo0612.carpool.presentation.booking.driver.decision.BookingDecisionAction
import com.juanpablo0612.carpool.presentation.booking.driver.decision.RejectionDraft
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTextField
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.reject_comment_placeholder
import enrutadoseia.composeapp.generated.resources.reject_confirm_button
import enrutadoseia.composeapp.generated.resources.reject_reason_other
import enrutadoseia.composeapp.generated.resources.reject_reason_pickup_not_possible
import enrutadoseia.composeapp.generated.resources.reject_reason_trip_cancelled
import enrutadoseia.composeapp.generated.resources.reject_reason_trip_full
import enrutadoseia.composeapp.generated.resources.reject_sheet_subtitle
import enrutadoseia.composeapp.generated.resources.reject_sheet_title
import org.jetbrains.compose.resources.stringResource

/**
 * Why the driver says no. A reason is required (the passenger's notification depends on it);
 * a comment is only offered for "other", where the reason alone says nothing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RejectBottomSheet(
    draft: RejectionDraft,
    onDecision: (BookingDecisionAction) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onDecision(BookingDecisionAction.DismissReject) },
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
                    text = stringResource(Res.string.reject_sheet_title),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = stringResource(Res.string.reject_sheet_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Column(modifier = Modifier.selectableGroup()) {
                RejectReason.entries.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .selectable(
                                selected = draft.reason == reason,
                                role = Role.RadioButton,
                                onClick = { onDecision(BookingDecisionAction.OnRejectReasonSelected(reason)) },
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    ) {
                        // The row handles the click, so the radio stays a plain indicator.
                        RadioButton(selected = draft.reason == reason, onClick = null)
                        Text(text = stringResource(reason.labelRes()), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            if (draft.reason == RejectReason.Other) {
                CarpoolTextField(
                    value = draft.comment,
                    onValueChange = { onDecision(BookingDecisionAction.OnRejectCommentChanged(it)) },
                    label = stringResource(Res.string.reject_reason_other),
                    placeholder = stringResource(Res.string.reject_comment_placeholder),
                    singleLine = false,
                    minLines = 2,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done,
                    ),
                )
            }

            PrimaryButton(
                text = stringResource(Res.string.reject_confirm_button),
                onClick = { onDecision(BookingDecisionAction.ConfirmReject) },
                enabled = draft.reason != null,
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            )
        }
    }
}

private fun RejectReason.labelRes() = when (this) {
    RejectReason.TripFull -> Res.string.reject_reason_trip_full
    RejectReason.TripCancelled -> Res.string.reject_reason_trip_cancelled
    RejectReason.PickupNotPossible -> Res.string.reject_reason_pickup_not_possible
    RejectReason.Other -> Res.string.reject_reason_other
}
