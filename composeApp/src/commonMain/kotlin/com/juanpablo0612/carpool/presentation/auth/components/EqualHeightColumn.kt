package com.juanpablo0612.carpool.presentation.auth.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A full-width column whose children all take the height of the tallest one, so a pair of
 * alternative choices reads as equally weighted even when one label wraps to more lines (large
 * font scales, narrow phones).
 */
@Composable
fun EqualHeightColumn(
    modifier: Modifier = Modifier,
    spacing: Dp = 0.dp,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val width = constraints.maxWidth
        val gap = spacing.roundToPx()
        val rowHeight = measurables.maxOfOrNull { it.maxIntrinsicHeight(width) } ?: 0
        val placeables = measurables.map { it.measure(Constraints.fixed(width, rowHeight)) }
        val totalHeight = placeables.sumOf { it.height } + gap * (placeables.size - 1).coerceAtLeast(0)
        layout(width, totalHeight) {
            var y = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(0, y)
                y += placeable.height + gap
            }
        }
    }
}
