package com.juanpablo0612.carpool.presentation.ui.util

import androidx.compose.runtime.Composable

/** Asks, where the platform needs it, for permission to save images to the photo gallery. */
fun interface GalleryWriteAccess {
    /** Calls [onResult] with whether saving may proceed, after the system dialog if one is shown. */
    fun request(onResult: (granted: Boolean) -> Unit)
}

@Composable
expect fun rememberGalleryWriteAccess(): GalleryWriteAccess
