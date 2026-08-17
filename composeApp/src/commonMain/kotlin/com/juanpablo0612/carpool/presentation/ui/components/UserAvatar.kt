package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing

private val avatarColors = listOf(
    Color(0xFF1565C0),
    Color(0xFF2E7D32),
    Color(0xFF6A1B9A),
    Color(0xFFAD1457),
    Color(0xFF00695C),
    Color(0xFFE65100),
    Color(0xFF4527A0),
    Color(0xFF283593),
)

@Composable
fun UserAvatar(
    name: String,
    photoUrl: String? = null,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier,
) {
    if (!photoUrl.isNullOrBlank()) {
        AsyncImage(
            model = photoUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(CircleShape),
        )
        return
    }

    val initial = remember(name) {
        name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    }
    val background = remember(name) {
        // mod() rather than abs(..) % ..: abs(Int.MIN_VALUE) is still negative, which would
        // index out of bounds for any palette size that doesn't divide it evenly.
        avatarColors[name.hashCode().mod(avatarColors.size)]
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        // Scale the initial with the circle so one component works from a 40.dp top-bar avatar
        // up to the 96.dp profile header without the letter looking lost.
        //
        // The circle itself is a fixed Dp size that never grows, so letting the letter track the
        // user's font-scale accessibility setting one-to-one (uncapped) risks the glyph outgrowing
        // its own circle at large scale settings. Capping — rather than dropping — the response is
        // deliberate: a `size.toSp()` conversion would cancel scale out entirely, making this the
        // only text in the app that ignores the setting; a locally-scoped, capped `Density` instead
        // lets it grow with the setting up to a ceiling that keeps the letter inside the circle.
        val density = LocalDensity.current
        val cappedDensity = remember(density) {
            Density(density.density, density.fontScale.coerceIn(1f, 1.3f))
        }
        CompositionLocalProvider(LocalDensity provides cappedDensity) {
            Text(
                text = initial,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = (size.value * 0.4f).sp),
                color = Color.White,
            )
        }
    }
}

@Preview
@Composable
private fun UserAvatarPreview() {
    CarpoolTheme {
        Row(
            modifier = Modifier.padding(Spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserAvatar(name = "Ana Torres", size = 40.dp)
            UserAvatar(name = "Bruno Diaz", size = 56.dp)
            UserAvatar(name = "Camila Ruiz", size = 96.dp)
        }
    }
}
