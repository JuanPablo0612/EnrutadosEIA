package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.LocalExtendedColors
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing

/** One row of a [RouteTimeline]. */
data class TimelineStop(
    val title: String,
    val caption: String? = null,
    /** The stop that matters to the viewer, e.g. where they get on; drawn in the accent colour. */
    val isHighlighted: Boolean = false,
)

/**
 * A route drawn top to bottom: a hollow marker for the origin, small dots for stops on the way
 * and a filled marker for the destination, joined by a rail. A highlighted stop (the viewer's
 * pickup) gets a larger marker and its caption in the accent colour.
 *
 * Each stop is plain text in reading order, so screen readers read the route as a list; the rail
 * and markers are decoration.
 */
@Composable
fun RouteTimeline(
    stops: List<TimelineStop>,
    modifier: Modifier = Modifier,
    rowSpacing: Dp = Spacing.md,
) {
    Column(modifier = modifier) {
        stops.forEachIndexed { index, stop ->
            val isFirst = index == 0
            val isLast = index == stops.lastIndex
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                TimelineRail(
                    marker = when {
                        stop.isHighlighted -> MarkerStyle.Highlight
                        isFirst -> MarkerStyle.Origin
                        isLast -> MarkerStyle.Destination
                        else -> MarkerStyle.Waypoint
                    },
                    showRailBelow = !isLast,
                )
                Column(
                    modifier = Modifier
                        .padding(start = Spacing.md)
                        .padding(bottom = if (isLast) 0.dp else rowSpacing),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = stop.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    stop.caption?.let {
                        Text(
                            text = it,
                            style = if (stop.isHighlighted) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodySmall,
                            color = if (stop.isHighlighted) {
                                LocalExtendedColors.current.warning
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                }
            }
        }
    }
}

private enum class MarkerStyle { Origin, Waypoint, Destination, Highlight }

/** The rail column: fixed width so every marker centres on the same line. */
private val RailWidth = 16.dp

@Composable
private fun TimelineRail(marker: MarkerStyle, showRailBelow: Boolean) {
    val primary = MaterialTheme.colorScheme.primary
    Column(
        modifier = Modifier
            .width(RailWidth)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Aligns the marker with the first line of the stop's title.
        Box(modifier = Modifier.height(4.dp))
        when (marker) {
            MarkerStyle.Origin -> Box(
                Modifier
                    .size(12.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest, CircleShape)
                    .border(2.5.dp, primary, CircleShape)
            )
            MarkerStyle.Destination -> Box(Modifier.size(12.dp).background(primary, CircleShape))
            MarkerStyle.Waypoint -> Box(
                Modifier.padding(vertical = 2.dp).size(8.dp).background(MaterialTheme.colorScheme.outline, CircleShape)
            )
            MarkerStyle.Highlight -> Box(Modifier.size(14.dp).background(LocalExtendedColors.current.warning, CircleShape))
        }
        if (showRailBelow) {
            Box(
                modifier = Modifier
                    .padding(vertical = 2.dp)
                    .width(2.dp)
                    .weight(1f)
                    // outlineVariant alone nearly vanishes on white cards; a softened outline
                    // keeps the rail visible without competing with the markers.
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = RAIL_ALPHA))
            )
        }
    }
}

private const val RAIL_ALPHA = 0.45f

@Preview
@Composable
private fun RouteTimelinePreview() {
    CarpoolTheme {
        RouteTimeline(
            stops = listOf(
                TimelineStop("Viva Envigado", "Salida · 6:30 p. m."),
                TimelineStop("Parque de Envigado", "Tú subes aquí", isHighlighted = true),
                TimelineStop("Zona Rosa"),
                TimelineStop("EIA · Sede Las Palmas", "Llegada"),
            ),
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}
