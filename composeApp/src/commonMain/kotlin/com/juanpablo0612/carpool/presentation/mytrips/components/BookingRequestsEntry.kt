package com.juanpablo0612.carpool.presentation.mytrips.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.arrow_forward_24px
import enrutadoseia.composeapp.generated.resources.booking_requests_title
import enrutadoseia.composeapp.generated.resources.inbox_24px
import enrutadoseia.composeapp.generated.resources.my_trips_pending_requests
import enrutadoseia.composeapp.generated.resources.my_trips_requests_subtitle
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * Always-visible entry to the booking requests screen from "Como conductor", so confirmed and
 * past requests stay reachable even when nothing is pending.
 */
@Composable
internal fun BookingRequestsEntry(
    pendingCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            leadingContent = {
                Icon(imageVector = vectorResource(Res.drawable.inbox_24px), contentDescription = null)
            },
            headlineContent = { Text(stringResource(Res.string.booking_requests_title)) },
            supportingContent = {
                Text(
                    if (pendingCount > 0) {
                        pluralStringResource(Res.plurals.my_trips_pending_requests, pendingCount, pendingCount)
                    } else {
                        stringResource(Res.string.my_trips_requests_subtitle)
                    }
                )
            },
            trailingContent = {
                if (pendingCount > 0) {
                    Badge { Text(pendingCount.toString()) }
                } else {
                    Icon(imageVector = vectorResource(Res.drawable.arrow_forward_24px), contentDescription = null)
                }
            },
        )
    }
}

@Preview
@Composable
private fun BookingRequestsEntryPreview() {
    CarpoolTheme {
        BookingRequestsEntry(pendingCount = 2, onClick = {})
    }
}
