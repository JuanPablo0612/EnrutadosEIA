package com.juanpablo0612.carpool.presentation.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Photos asks for add-only access by itself on the first save (NSPhotoLibraryAddUsageDescription),
 * and a refusal surfaces as a failed save, so nothing needs asking up front.
 */
@Composable
actual fun rememberGalleryWriteAccess(): GalleryWriteAccess = remember { GalleryWriteAccess { it(true) } }
