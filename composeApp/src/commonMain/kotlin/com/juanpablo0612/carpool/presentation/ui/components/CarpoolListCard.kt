package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Elevation
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing

/**
 * The one card recipe every list row in the app renders through.
 *
 * Before this, the nine equivalent list rows disagreed on four container colours, three shapes,
 * three elevations and three padding expressions. This fixes the container to one recipe:
 * [MaterialTheme.shapes] `large`, [Elevation.card], [Spacing.cardPadding] inset, and the
 * **default M3 tonal container** — deliberately not forced to `surface`, which cancels the tonal
 * elevation cue and leaves shape as the only way to tell a card apart from its surroundings.
 *
 * Pass [onClick] for a tappable row: it renders through the `Card(onClick = ...)` overload, which
 * gives a ripple correctly clipped to the card's shape and the right accessibility click role.
 * Omit it for a row that isn't tappable. [colors] defaults to the plain tonal container and only
 * needs overriding for a deliberately distinct variant, such as [HighlightCard]'s
 * primary-container hero treatment.
 */
@Composable
fun CarpoolListCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    colors: CardColors = CardDefaults.cardColors(),
    content: @Composable ColumnScope.() -> Unit,
) {
    val cardModifier = modifier.fillMaxWidth()
    val shape = MaterialTheme.shapes.large
    val elevation = CardDefaults.cardElevation(defaultElevation = Elevation.card)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = cardModifier,
            shape = shape,
            colors = colors,
            elevation = elevation,
        ) {
            Column(modifier = Modifier.padding(Spacing.cardPadding), content = content)
        }
    } else {
        Card(
            modifier = cardModifier,
            shape = shape,
            colors = colors,
            elevation = elevation,
        ) {
            Column(modifier = Modifier.padding(Spacing.cardPadding), content = content)
        }
    }
}

@Preview
@Composable
private fun CarpoolListCardStaticPreview() {
    CarpoolTheme {
        CarpoolListCard {
            Text("Static list card")
        }
    }
}

@Preview
@Composable
private fun CarpoolListCardClickablePreview() {
    CarpoolTheme {
        CarpoolListCard(onClick = {}) {
            Text("Clickable list card")
        }
    }
}
