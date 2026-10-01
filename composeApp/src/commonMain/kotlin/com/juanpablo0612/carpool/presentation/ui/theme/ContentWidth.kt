package com.juanpablo0612.carpool.presentation.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Maximum widths for screen content on wide windows (tablets, unfolded foldables, landscape).
 *
 * Past these widths, lines of text and form fields become hard to scan, so content stays
 * centred at this width and the extra space becomes margin. On a phone in portrait the window is
 * narrower than all of them and nothing changes.
 */
object ContentWidth {
    /** Single-column forms and auth screens. */
    val form = 600.dp

    /** Lists and detail screens. */
    val list = 840.dp

    /** Minimum cell width when a card list becomes an adaptive grid. */
    val gridCell = 360.dp
}
