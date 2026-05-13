package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
    size: Dp = 48.dp,
    modifier: Modifier = Modifier,
) {
    val initial = remember(name) {
        name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    }
    val background = remember(name) {
        avatarColors[Math.abs(name.hashCode()) % avatarColors.size]
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
        )
    }
}
