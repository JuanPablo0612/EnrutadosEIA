package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Requires: add `remove_24px` vector to composeResources/drawable/ (Material Symbol "remove")
// and swap Text("−") / Text("+") for Icon(vectorResource(…)) once the asset is added.
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
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilledIconButton(
            onClick = { if (value > min) onChange(value - 1) },
            enabled = value > min,
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            modifier = Modifier.size(36.dp)
        ) {
            Text(text = "−", fontSize = 20.sp)
        }
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge
        )
        FilledIconButton(
            onClick = { if (value < max) onChange(value + 1) },
            enabled = value < max,
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            modifier = Modifier.size(36.dp)
        ) {
            Text(text = "+", fontSize = 20.sp)
        }
    }
}
