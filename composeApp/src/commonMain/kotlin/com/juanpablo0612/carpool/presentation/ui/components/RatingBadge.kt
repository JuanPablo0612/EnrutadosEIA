package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.rating.model.RatingSummary
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.LocalExtendedColors
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.cd_rating
import enrutadoseia.composeapp.generated.resources.rating_count
import enrutadoseia.composeapp.generated.resources.rating_value
import enrutadoseia.composeapp.generated.resources.star_24px
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import kotlin.math.roundToInt

/**
 * A rating as a star and its average ("★ 4,8"), optionally with how many ratings back it. The
 * decimal separator follows the locale, and screen readers hear "Calificación de 4,8 sobre 5"
 * rather than the glyph.
 */
@Composable
fun RatingBadge(
    rating: RatingSummary,
    modifier: Modifier = Modifier,
    showCount: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.titleSmall,
) {
    val value = formatRatingValue(rating.average)
    val description = stringResource(Res.string.cd_rating, value)
    val count = if (showCount) pluralStringResource(Res.plurals.rating_count, rating.count, rating.count) else null
    Row(
        modifier = modifier.clearAndSetSemantics {
            contentDescription = listOfNotNull(description, count).joinToString(", ")
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.star_24px),
            contentDescription = null,
            tint = LocalExtendedColors.current.rating,
            modifier = Modifier.size(16.dp),
        )
        Text(text = value, style = textStyle)
        if (count != null) {
            Text(
                text = "· $count",
                style = textStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** One decimal, with the locale's separator: 4.83 → "4,8" in Spanish. */
@Composable
fun formatRatingValue(average: Double): String {
    val tenths = (average.coerceIn(0.0, 5.0) * 10).roundToInt()
    return stringResource(Res.string.rating_value, tenths / 10, tenths % 10)
}

@Preview
@Composable
private fun RatingBadgePreview() {
    CarpoolTheme {
        RatingBadge(rating = RatingSummary(average = 4.83, count = 27), showCount = true)
    }
}
