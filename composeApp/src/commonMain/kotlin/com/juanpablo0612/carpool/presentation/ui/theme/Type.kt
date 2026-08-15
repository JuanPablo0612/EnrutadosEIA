package com.juanpablo0612.carpool.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * The app's type scale.
 *
 * Only the roles listed here deviate from Material 3's defaults. The roles deliberately left
 * unlisted — `displayLarge`, `titleSmall`, `bodySmall`, `labelSmall` — resolve to the stock M3
 * styles, which already carry tuned `lineHeight`/`letterSpacing` values that this file does not
 * specify. Redefining them here with fontSize/fontWeight alone would silently drop those
 * metrics, so they stay stock on purpose:
 *
 *  - `bodySmall`  (12sp) — secondary card metadata; the second-most-used role in the app
 *  - `labelSmall` (11sp) — dense inline labels
 *  - `titleSmall` (14sp) — reserved; prefer `titleMedium` for card titles and `labelLarge` for
 *                          emphasised inline values, so the two 14sp roles don't compete
 *  - `displayLarge` (57sp) — used once, for the onboarding illustration glyph
 *
 * Canonical usage:
 *  | Screen title (top bar)     | titleLarge   |
 *  | In-content section header  | titleMedium  |
 *  | Empty / error state title  | headlineSmall|
 *  | List-card title            | titleMedium  |
 *  | Card body                  | bodyMedium   |
 *  | Card secondary metadata    | bodySmall    |
 *  | Emphasised inline value    | labelLarge   |
 *  | Badge / chip text          | labelMedium  |
 */
val AppTypography = Typography(
    displaySmall = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.W400),
    headlineSmall = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
    // Bold rather than SemiBold: 13 screens were re-applying `.copy(fontWeight = Bold)` to this
    // role for their top-bar title. Carrying the weight on the token lets those overrides go.
    titleLarge = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium),
)
