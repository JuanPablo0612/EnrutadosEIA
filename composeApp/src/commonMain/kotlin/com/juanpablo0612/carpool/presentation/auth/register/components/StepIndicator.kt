package com.juanpablo0612.carpool.presentation.auth.register.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.auth.validation.PasswordStrength
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.LocalExtendedColors
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.password_strength_medium
import enrutadoseia.composeapp.generated.resources.password_strength_strong
import enrutadoseia.composeapp.generated.resources.password_strength_weak
import org.jetbrains.compose.resources.stringResource

@Composable
fun StepIndicator(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        // Not a Spacing step: the 4dp/8dp scale reads too loose for these compact pills.
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { index ->
            val isCompleted = index < current - 1
            val isCurrent = index == current - 1
            val width = if (isCurrent) Spacing.xl else Spacing.sm
            Box(
                modifier = Modifier
                    .size(width = width, height = Spacing.sm)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted || isCurrent -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.outlineVariant
                        }
                    )
            )
        }
    }
}

@Composable
fun PasswordStrengthIndicator(
    strength: PasswordStrength,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalExtendedColors.current
    val (progress, color, label) = when (strength) {
        PasswordStrength.Weak -> Triple(
            1f / 3f,
            MaterialTheme.colorScheme.error,
            stringResource(Res.string.password_strength_weak),
        )
        PasswordStrength.Medium -> Triple(
            2f / 3f,
            extendedColors.warning,
            stringResource(Res.string.password_strength_medium),
        )
        PasswordStrength.Strong -> Triple(
            1f,
            extendedColors.success,
            stringResource(Res.string.password_strength_strong),
        )
    }
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "strength")

    Column(modifier = modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                // Genuine hairline, not a Spacing step: the strength bar's own thickness.
                .height(4.dp)
                .clip(MaterialTheme.shapes.extraSmall),
            color = color,
            trackColor = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Preview
@Composable
private fun StepIndicatorPreview() {
    CarpoolTheme {
        StepIndicator(current = 2, total = 4, modifier = Modifier.padding(Spacing.lg))
    }
}

@Preview
@Composable
private fun PasswordStrengthIndicatorPreview() {
    CarpoolTheme {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            PasswordStrengthIndicator(strength = PasswordStrength.Weak)
            Spacer(modifier = Modifier.height(Spacing.md))
            PasswordStrengthIndicator(strength = PasswordStrength.Medium)
            Spacer(modifier = Modifier.height(Spacing.md))
            PasswordStrengthIndicator(strength = PasswordStrength.Strong)
        }
    }
}
