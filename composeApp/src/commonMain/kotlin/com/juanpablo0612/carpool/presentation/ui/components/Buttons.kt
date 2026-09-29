package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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

/** Minimum height of the app's full-width buttons; `heightIn` so labels survive large font scales. */
private val ButtonMinHeight = 52.dp

/**
 * The screen's main call to action. Full width by default: in a [androidx.compose.foundation.layout.Row]
 * give it `Modifier.weight(1f)` to share the row with a [SecondaryButton].
 */
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
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = ButtonMinHeight),
        enabled = enabled && !isLoading,
        shape = shape,
        // Disabled colours are left to ButtonDefaults, which already applies the spec-correct
        // 0.12 container / 0.38 content opacities.
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
            ButtonContent(text = text, leadingIcon = leadingIcon, trailingIcon = trailingIcon)
        }
    }
}

/** A secondary action next to or below a [PrimaryButton]; outlined so it never competes with it. */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.medium,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    leadingIcon: ImageVector? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = ButtonMinHeight),
        enabled = enabled,
        shape = shape,
        border = ButtonDefaults.outlinedButtonBorder(enabled),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = contentColor
        )
    ) {
        ButtonContent(text = text, leadingIcon = leadingIcon, trailingIcon = null)
    }
}

@Composable
private fun ButtonContent(text: String, leadingIcon: ImageVector?, trailingIcon: ImageVector?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterHorizontally)
    ) {
        if (leadingIcon != null) {
            Icon(imageVector = leadingIcon, contentDescription = null, modifier = Modifier.size(20.dp))
        }
        Text(text = text, style = MaterialTheme.typography.titleMedium)
        if (trailingIcon != null) {
            Icon(imageVector = trailingIcon, contentDescription = null, modifier = Modifier.size(20.dp))
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
private fun LinkTextPreview() {
    CarpoolTheme {
        LinkText(text = "Forgot your password?", onClick = {}, modifier = Modifier.padding(Spacing.lg))
    }
}
