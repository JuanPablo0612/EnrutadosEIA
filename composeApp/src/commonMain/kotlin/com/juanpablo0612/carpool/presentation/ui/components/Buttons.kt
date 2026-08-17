package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    shape: Shape = MaterialTheme.shapes.medium,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    trailingIcon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            // heightIn, not height: this is the app's universal CTA, and a fixed height clips
            // its label at large system font scales.
            .heightIn(min = 56.dp),
        enabled = enabled && !isLoading,
        shape = shape,
        // Disabled colours are left to ButtonDefaults, which already applies the spec-correct
        // 0.12 container / 0.38 content opacities; the previous 0.5f override was less legible
        // than the default it replaced.
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(Spacing.xl),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Icon(imageVector = trailingIcon, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.medium,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.primary
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            // heightIn so the label survives large font scales; see PrimaryButton.
            .heightIn(min = 56.dp),
        enabled = enabled,
        shape = shape,
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = contentColor
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * The app's middle emphasis tier, between [PrimaryButton]'s filled CTA and [SecondaryButton]'s
 * outline: a [FilledTonalButton] for actions that matter but shouldn't compete with the screen's
 * one primary action (e.g. a secondary but still-affirmative choice). Same API shape as
 * [PrimaryButton] so call sites can switch emphasis without re-plumbing loading/enabled state.
 */
@Composable
fun TertiaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    shape: Shape = MaterialTheme.shapes.medium,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    trailingIcon: ImageVector? = null
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        enabled = enabled && !isLoading,
        shape = shape,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(Spacing.xl),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Icon(imageVector = trailingIcon, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun LinkText(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    fontWeight: FontWeight = FontWeight.Bold
) {
    Text(
        text = text,
        modifier = modifier.clickable { onClick() },
        color = color,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = fontWeight)
    )
}

@Preview
@Composable
private fun PrimaryButtonPreview() {
    CarpoolTheme {
        Column {
            PrimaryButton(text = "Continue", onClick = {}, modifier = Modifier.padding(Spacing.lg))
            PrimaryButton(text = "Loading", onClick = {}, isLoading = true, modifier = Modifier.padding(Spacing.lg))
        }
    }
}

@Preview
@Composable
private fun SecondaryButtonPreview() {
    CarpoolTheme {
        SecondaryButton(text = "Cancel", onClick = {}, modifier = Modifier.padding(Spacing.lg))
    }
}

@Preview
@Composable
private fun TertiaryButtonPreview() {
    CarpoolTheme {
        Column {
            TertiaryButton(text = "Maybe later", onClick = {}, modifier = Modifier.padding(Spacing.lg))
            TertiaryButton(text = "Loading", onClick = {}, isLoading = true, modifier = Modifier.padding(Spacing.lg))
        }
    }
}

@Preview
@Composable
private fun LinkTextPreview() {
    CarpoolTheme {
        LinkText(text = "Forgot your password?", onClick = {}, modifier = Modifier.padding(Spacing.lg))
    }
}
