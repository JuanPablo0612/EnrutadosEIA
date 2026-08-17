package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing

@Composable
private fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val surface = MaterialTheme.colorScheme.surface
    return Brush.linearGradient(
        colors = listOf(surfaceVariant, surface, surfaceVariant),
        start = Offset(translateX - 500f, 0f),
        end = Offset(translateX, 0f)
    )
}

@Composable
fun ListSkeleton(itemCount: Int = 6, modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    Column(
        modifier = modifier.padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        repeat(itemCount) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                // 48dp avatar placeholder — same touch-target-sized constant as NumberStepper's
                // STEPPER_TOUCH_TARGET, not a Spacing step.
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .heightIn(min = 48.dp)
                        .background(brush, MaterialTheme.shapes.small)
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    // Text-line placeholders: heightIn (not height) so they don't clip a shimmer
                    // frame that happens to render at a large system font scale.
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .heightIn(min = Spacing.lg)
                            .background(brush, MaterialTheme.shapes.extraSmall)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .heightIn(min = Spacing.md)
                            .background(brush, MaterialTheme.shapes.extraSmall)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailSkeleton(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    Column(
        modifier = modifier.padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        // Hero image placeholder: a fixed height is correct here (it stands in for an image,
        // not text), so it's left as a literal rather than heightIn.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(brush, MaterialTheme.shapes.medium)
        )
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            // Text-line placeholders: heightIn so a large font scale can't clip them.
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .heightIn(min = 20.dp)
                    .background(brush, MaterialTheme.shapes.extraSmall)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .heightIn(min = 14.dp)
                    .background(brush, MaterialTheme.shapes.extraSmall)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .heightIn(min = 14.dp)
                    .background(brush, MaterialTheme.shapes.extraSmall)
            )
        }
        Spacer(modifier = Modifier.height(Spacing.sm))
        repeat(3) {
            // 56dp row placeholder — the same list-row/button height constant used elsewhere
            // (e.g. PrimaryButton's min height), not a Spacing step.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .background(brush, MaterialTheme.shapes.small)
            )
        }
    }
}

@Preview
@Composable
private fun ListSkeletonPreview() {
    CarpoolTheme {
        ListSkeleton(itemCount = 3, modifier = Modifier.padding(vertical = Spacing.lg))
    }
}

@Preview
@Composable
private fun DetailSkeletonPreview() {
    CarpoolTheme {
        DetailSkeleton()
    }
}
