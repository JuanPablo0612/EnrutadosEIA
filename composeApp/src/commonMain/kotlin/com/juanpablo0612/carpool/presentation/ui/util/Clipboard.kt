package com.juanpablo0612.carpool.presentation.ui.util

import androidx.compose.ui.platform.ClipEntry

/** A plain-text clipboard entry; building one is platform-specific in Compose Multiplatform. */
expect fun plainTextClipEntry(text: String): ClipEntry
