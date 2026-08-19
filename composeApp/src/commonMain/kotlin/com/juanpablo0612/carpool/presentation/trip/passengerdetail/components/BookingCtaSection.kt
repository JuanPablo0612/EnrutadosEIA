package com.juanpablo0612.carpool.presentation.trip.passengerdetail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.trip.passengerdetail.RouteDetailPassengerAction
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.book_request_button
import enrutadoseia.composeapp.generated.resources.book_request_sent
import enrutadoseia.composeapp.generated.resources.book_request_subtext
import enrutadoseia.composeapp.generated.resources.no_seats_available
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BookingCtaSection(
    availableSeats: Int,
    alreadyRequested: Boolean,
    isBooking: Boolean,
    onAction: (RouteDetailPassengerAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        when {
            alreadyRequested -> Button(
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.book_request_sent))
            }

            availableSeats <= 0 -> Button(
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.no_seats_available))
            }

            else -> {
                Button(
                    onClick = { onAction(RouteDetailPassengerAction.OnOpenConfirmSheet) },
                    enabled = !isBooking,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isBooking) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(Res.string.book_request_button))
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = stringResource(Res.string.book_request_subtext),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
