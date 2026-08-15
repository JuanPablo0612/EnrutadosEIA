package com.juanpablo0612.carpool.presentation.ui.theme

/**
 * Named opacities for the few places a theme colour is deliberately softened.
 *
 * Reach for a paired `*Container` / `on*Container` colour role before reaching for these — an
 * alpha composite of a colour against itself is the usual cause of a contrast failure. These
 * exist so the values that *do* remain are consistent and reviewable rather than re-derived at
 * each call site.
 *
 * Note there is no `disabled` constant: Material 3's `ButtonDefaults` and friends already apply
 * spec-correct disabled opacities (0.12 container / 0.38 content). Let them.
 */
object Alpha {
    /** Tinted container behind a status pill or chip. */
    const val BADGE_CONTAINER = 0.12f

    /** Secondary text or icons that must recede without losing legibility. */
    const val DEEMPHASIS = 0.6f

    /** Scrim over imagery, so overlaid text stays readable. */
    const val SCRIM = 0.55f
}
