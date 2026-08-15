package com.juanpablo0612.carpool.presentation.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Elevation steps, both shadow and tonal.
 *
 * Prefer letting Material 3's tonal surface containers carry separation and keeping cards at
 * [card]; reach for [raised] only when an element must read as floating above its siblings.
 */
object Elevation {
    /** Flat against its parent surface. */
    val none = 0.dp

    /** Resting list-item card. */
    val card = 1.dp

    /** Emphasised card or menu. */
    val raised = 3.dp

    /** Dialog or bottom sheet. */
    val overlay = 6.dp
}
