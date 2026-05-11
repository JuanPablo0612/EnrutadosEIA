package com.juanpablo0612.carpool.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.confirm_button
import enrutadoseia.composeapp.generated.resources.home_pending_requests_title
import enrutadoseia.composeapp.generated.resources.home_see_all_requests
import enrutadoseia.composeapp.generated.resources.reject_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun PendingRequestsSection(
    requests: List<Booking>,
    onAccept: (String) -> Unit,
    onReject: (String) -> Unit,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.home_pending_requests_title, requests.size),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 1.dp,
        ) {
            Column {
                requests.take(3).forEachIndexed { index, booking ->
                    PendingRequestRow(
                        booking = booking,
                        onAccept = { onAccept(booking.id) },
                        onReject = { onReject(booking.id) },
                        modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md),
                    )
                    if (index < requests.take(3).lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
                    }
                }
            }
        }
        if (requests.size > 0) {
            TextButton(
                onClick = onSeeAll,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(stringResource(Res.string.home_see_all_requests))
            }
        }
    }
}

@Composable
private fun PendingRequestRow(
    booking: Booking,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = booking.passengerName,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = "${booking.originName} → ${booking.destinationName}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            OutlinedButton(onClick = onReject) {
                Text(stringResource(Res.string.reject_button))
            }
            OutlinedButton(onClick = onAccept) {
                Text(stringResource(Res.string.confirm_button))
            }
        }
    }
}
