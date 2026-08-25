package com.juanpablo0612.carpool.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.presentation.ui.components.BookingStatusBadge
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.components.RouteLineRow
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing

/**
 * The shared shape of the home screen's "what's next" hero card.
 *
 * `NextTripCard` and `NextBookingCard` were near-byte-identical 87-line files, differing only in
 * their model type, which status badge they show, one string resource, and one field name. This
 * collapses both into a single component: the two files now just supply their model-specific
 * bits (title, badge, time text, route) and render through here.
 *
 * Renders through [CarpoolListCard] with a primary-container variant so the "next up" card reads
 * as visually distinct from the plain list rows around it, and through [RouteLineRow] for its
 * origin/destination line.
 */
@Composable
fun HighlightCard(
    title: String,
    statusBadge: @Composable () -> Unit,
    timeText: String,
    origin: String,
    destination: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CarpoolListCard(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            statusBadge()
        }
        Spacer(modifier = Modifier.height(Spacing.sm))
        Text(
            text = timeText,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        RouteLineRow(
            origin = origin,
            destination = destination,
            style = MaterialTheme.typography.bodyMedium,
            textColor = MaterialTheme.colorScheme.onPrimaryContainer,
            iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Preview
@Composable
private fun HighlightCardPreview() {
    CarpoolTheme {
        HighlightCard(
            title = "Next trip",
            statusBadge = { BookingStatusBadge(status = BookingStatus.Confirmed) },
            timeText = "In 25 min",
            origin = "Universidad EIA — Sede Las Palmas",
            destination = "Centro Comercial Santafé, Envigado",
            onClick = {},
        )
    }
}
