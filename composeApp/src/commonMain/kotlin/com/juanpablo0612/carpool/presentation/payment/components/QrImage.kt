package com.juanpablo0612.carpool.presentation.payment.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing

/**
 * A QR code image, square and on white whatever the theme, so a bank app pointed at the screen
 * still reads it. Fits rather than crops: a cropped code doesn't scan. [model] is a URL or a
 * picked file.
 */
@Composable
internal fun QrImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    maxWidth: Dp = 320.dp,
) {
    Box(
        modifier = modifier
            .widthIn(max = maxWidth)
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
            .background(Color.White)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium)
            .padding(Spacing.sm),
    ) {
        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
