package com.juanpablo0612.carpool.presentation.ui.theme

import androidx.compose.ui.unit.dp

/**
 * The app's spacing scale.
 *
 * The raw steps cover layout rhythm; the semantic aliases below name the two recurring insets so
 * screens stop re-deciding them. The app deliberately runs **two** screen gutters: list and
 * detail screens use 16dp, while single-column form/auth screens use a wider 24dp. Use the
 * semantic alias rather than the raw step at screen level, so the distinction stays legible.
 *
 * Values outside this scale (1–3dp hairlines, timeline rails) stay as literals — they are
 * strokes, not spacing.
 */
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp

    /** Horizontal gutter for list and detail screens. */
    val screenHorizontal = lg

    /** Horizontal gutter for single-column form and auth screens. */
    val screenHorizontalForm = xl

    /** Standard inset inside a list-item card. */
    val cardPadding = lg
}
