package com.juanpablo0612.carpool.presentation.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * The app's corner-radius scale.
 *
 * These values are Material 3's defaults, stated explicitly. Wiring them into `MaterialTheme`
 * changes nothing on screen; it gives the ~34 hand-written `RoundedCornerShape(N.dp)` call sites
 * a named token to point at, and one place to tune the app's roundness.
 *
 * Canonical assignment:
 *  | Badge, chip, skeleton block | extraSmall |
 *  | Image clip, small surface   | small      |
 *  | Text field, button          | medium     |
 *  | List-item card              | large      |
 *  | Dialog, bottom sheet        | extraLarge |
 *
 * Genuinely circular affordances (avatars, dots, steppers) use `CircleShape` directly, and chat
 * bubbles use an asymmetric shape — neither belongs on this scale.
 */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)
