package com.juanpablo0612.carpool.presentation.ui.util

import androidx.compose.ui.tooling.preview.Preview

/**
 * Renders a screen at the sizes where layouts break: a small phone, the same phone in
 * landscape, a tablet, and a small phone at the largest system font scale.
 */
@Preview(name = "Small phone", widthDp = 360, heightDp = 640)
@Preview(name = "Phone landscape", widthDp = 800, heightDp = 360)
@Preview(name = "Tablet", widthDp = 1280, heightDp = 800)
@Preview(name = "Large font", widthDp = 360, heightDp = 640, fontScale = 2f)
annotation class ScreenPreviews
