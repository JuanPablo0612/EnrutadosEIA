package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.star_24px
import org.jetbrains.compose.resources.vectorResource

/**
 * A driver's average rating, shown wherever a passenger evaluates who they'd be riding with
 * (search results, trip detail).
 */
@Composable
fun DriverRatingBadge(
    averageRating: Double,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Icon(
            imageVector = vectorResource(Res.drawable.star_24px),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(Spacing.xs))
        Text(
            text = formatRating(averageRating),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatRating(rating: Double): String {
    val clamped = rating.coerceIn(0.0, 5.0)
    val tenths = (clamped * 10).toInt()
    return "${tenths / 10}.${tenths % 10}"
}

@Preview
@Composable
private fun DriverRatingBadgePreview() {
    CarpoolTheme {
        DriverRatingBadge(averageRating = 4.8)
    }
}
