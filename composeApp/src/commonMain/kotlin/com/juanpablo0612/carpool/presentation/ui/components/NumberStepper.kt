package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_24px
import enrutadoseia.composeapp.generated.resources.cd_decrease
import enrutadoseia.composeapp.generated.resources.cd_increase
import enrutadoseia.composeapp.generated.resources.remove_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/** The visible button; the touch target around it is [STEPPER_TOUCH_TARGET]. */
private val STEPPER_BUTTON_SIZE = 36.dp

/**
 * Material's minimum interactive size. A caller-supplied `Modifier.size` sits outside
 * `FilledIconButton`'s own `minimumInteractiveComponentSize()` and clamps it, so the target has to
 * be restored explicitly rather than left to the component.
 */
private val STEPPER_TOUCH_TARGET = 48.dp

@Composable
fun NumberStepper(
    value: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    min: Int = 1,
    max: Int = Int.MAX_VALUE,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        StepperButton(
            onClick = { if (value > min) onChange(value - 1) },
            enabled = value > min,
            icon = Res.drawable.remove_24px,
            contentDescription = stringResource(Res.string.cd_decrease),
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge
        )
        StepperButton(
            onClick = { if (value < max) onChange(value + 1) },
            enabled = value < max,
            icon = Res.drawable.add_24px,
            contentDescription = stringResource(Res.string.cd_increase),
        )
    }
}

@Composable
private fun StepperButton(
    onClick: () -> Unit,
    enabled: Boolean,
    icon: org.jetbrains.compose.resources.DrawableResource,
    contentDescription: String,
) {
    Box(
        modifier = Modifier.size(STEPPER_TOUCH_TARGET),
        contentAlignment = Alignment.Center,
    ) {
        FilledIconButton(
            onClick = onClick,
            enabled = enabled,
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            modifier = Modifier.size(STEPPER_BUTTON_SIZE)
        ) {
            Icon(
                imageVector = vectorResource(icon),
                contentDescription = contentDescription,
                modifier = Modifier.size(Spacing.xl)
            )
        }
    }
}
