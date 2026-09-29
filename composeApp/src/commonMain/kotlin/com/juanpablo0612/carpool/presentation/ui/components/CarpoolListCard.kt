package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Elevation
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing

/**
 * The one card recipe every list row in the app renders through.
 *
 * A white ([androidx.compose.material3.ColorScheme.surfaceContainerLowest]) card with a hairline
 * [androidx.compose.material3.ColorScheme.outlineVariant] border on the neutral screen ground, in
 * [MaterialTheme.shapes] `large` with a [Spacing.cardPadding] inset. It is flat: the border and
 * the tonal step from the background carry the separation, so a list of cards doesn't turn into
 * a stack of shadows.
 *
 * Pass [onClick] for a tappable row: it renders through the `Card(onClick = ...)` overload, which
 * gives a ripple correctly clipped to the card's shape and the right accessibility click role.
 * Omit it for a row that isn't tappable. Override [colors] and [border] only for a deliberately
 * distinct variant, such as a highlighted or warning card.
 */
@Composable
fun CarpoolListCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    colors: CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    ),
    border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    contentPadding: PaddingValues = PaddingValues(Spacing.cardPadding),
    content: @Composable ColumnScope.() -> Unit,
) {
    val cardModifier = modifier.fillMaxWidth()
    val shape = MaterialTheme.shapes.large
    val elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = cardModifier,
            shape = shape,
            colors = colors,
            elevation = elevation,
            border = border,
        ) {
            Column(modifier = Modifier.padding(contentPadding), content = content)
        }
    } else {
        Card(
            modifier = cardModifier,
            shape = shape,
            colors = colors,
            elevation = elevation,
            border = border,
        ) {
            Column(modifier = Modifier.padding(contentPadding), content = content)
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
