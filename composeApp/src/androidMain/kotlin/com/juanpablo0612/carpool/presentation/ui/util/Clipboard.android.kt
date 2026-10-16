package com.juanpablo0612.carpool.presentation.ui.util

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.toClipEntry

actual fun plainTextClipEntry(text: String): ClipEntry = ClipData.newPlainText(text, text).toClipEntry()
