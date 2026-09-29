package com.juanpablo0612.carpool.presentation.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * The app's corner-radius scale.
 *
 * Softer than Material's defaults: cards and fields round enough to read as friendly, touchable
 * objects, while staying a step below the fully rounded pills used for chips and status badges.
 *
 * Canonical assignment:
 *  | Skeleton block, inline tag  | extraSmall |
 *  | Segmented-control thumb     | small      |
 *  | Text field, button          | medium     |
 *  | List-item card              | large      |
 *  | Dialog, bottom sheet        | extraLarge |
 *
 * Genuinely circular affordances (avatars, dots, steppers, chips, status pills) use `CircleShape`
 * directly, and chat bubbles use an asymmetric shape — neither belongs on this scale.
 */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)
