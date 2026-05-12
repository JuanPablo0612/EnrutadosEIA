package com.juanpablo0612.carpool.presentation.routes.create.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TrajectoryConnector(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
    height: Dp = 32.dp
) {
    Canvas(modifier = modifier) {
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
        drawLine(
            color = color,
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height),
            strokeWidth = 2.dp.toPx(),
            pathEffect = pathEffect
        )
    }
}
