package com.juanpablo0612.carpool.presentation.bookings.driver.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.juanpablo0612.carpool.domain.booking.model.RejectReason
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.cancel_button
import enrutadoseia.composeapp.generated.resources.reject_comment_placeholder
import enrutadoseia.composeapp.generated.resources.reject_confirm_button
import enrutadoseia.composeapp.generated.resources.reject_reason_other
import enrutadoseia.composeapp.generated.resources.reject_reason_pickup_not_possible
import enrutadoseia.composeapp.generated.resources.reject_reason_trip_cancelled
import enrutadoseia.composeapp.generated.resources.reject_reason_trip_full
import enrutadoseia.composeapp.generated.resources.reject_sheet_subtitle
import enrutadoseia.composeapp.generated.resources.reject_sheet_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RejectBottomSheet(
    selectedReason: RejectReason?,
    comment: String,
    onSelectReason: (RejectReason) -> Unit,
    onCommentChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
                .navigationBarsPadding(),
        ) {
            Text(
                text = stringResource(Res.string.reject_sheet_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = stringResource(Res.string.reject_sheet_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(Spacing.md))

            RejectReason.entries.forEach { reason ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    RadioButton(
                        selected = selectedReason == reason,
                        onClick = { onSelectReason(reason) },
                    )
                    Text(
                        text = stringResource(reason.labelRes()),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            if (selectedReason == RejectReason.Other) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                OutlinedTextField(
                    value = comment,
                    onValueChange = onCommentChange,
                    placeholder = {
                        Text(
                            text = stringResource(Res.string.reject_comment_placeholder),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                )
            }

            Spacer(modifier = Modifier.height(Spacing.lg))
            Button(
                onClick = onConfirm,
                enabled = selectedReason != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(Res.string.reject_confirm_button))
            }
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(Res.string.cancel_button))
            }
            Spacer(modifier = Modifier.height(Spacing.sm))
        }
    }
}

private fun RejectReason.labelRes() = when (this) {
    RejectReason.TripFull -> Res.string.reject_reason_trip_full
    RejectReason.TripCancelled -> Res.string.reject_reason_trip_cancelled
    RejectReason.PickupNotPossible -> Res.string.reject_reason_pickup_not_possible
    RejectReason.Other -> Res.string.reject_reason_other
}
