package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.arrow_forward_24px
import enrutadoseia.composeapp.generated.resources.cd_route_line
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * An "origin → destination" row, deduplicated from six near-identical copies that each hard-clipped
 * long place names (no [TextOverflow.Ellipsis]), read as three separate accessibility nodes, and
 * disagreed on the connecting icon's size (12/14/16/18dp across the six).
 *
 * Fixes all three: both texts ellipsize, the icon is a single [RouteLineIconSize], and the whole
 * row is merged into one accessibility node via [Res.string.cd_route_line] so a screen reader
 * announces the route once instead of three fragments.
 *
 * [style]/[textColor]/[iconTint] default to the row's most common call-site look (semibold body
 * text, muted icon); override them for a variant with different emphasis or a tinted container
 * (e.g. [HighlightCard]'s primary-container hero card).
 */
@Composable
fun RouteLineRow(
    origin: String,
    destination: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
    textColor: Color = LocalContentColor.current,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    val description = stringResource(Res.string.cd_route_line, origin, destination)
    Row(
        modifier = modifier.clearAndSetSemantics { contentDescription = description },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = origin,
            style = style,
            color = textColor,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Icon(
            imageVector = vectorResource(Res.drawable.arrow_forward_24px),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier
                .padding(horizontal = Spacing.xs)
                .size(RouteLineIconSize)
        )
        Text(
            text = destination,
            style = style,
            color = textColor,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** The one icon size every route row now shares, replacing the 12/14/16/18dp spread. */
private val RouteLineIconSize = 16.dp

@Preview
@Composable
private fun RouteLineRowPreview() {
    CarpoolTheme {
        RouteLineRow(
            origin = "Universidad EIA — Sede Las Palmas",
            destination = "Centro Comercial Santafé, Envigado"
        )
    }
}
