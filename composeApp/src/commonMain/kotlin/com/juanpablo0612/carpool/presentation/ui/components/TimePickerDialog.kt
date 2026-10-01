package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Elevation
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.WindowLayout
import com.juanpablo0612.carpool.presentation.ui.util.rememberWindowLayout
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.cancel_button
import enrutadoseia.composeapp.generated.resources.ok_button
import enrutadoseia.composeapp.generated.resources.select_time_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun TimePickerDialog(
    title: String = stringResource(Res.string.select_time_title),
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = Elevation.overlay,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            // Scrolls so the buttons stay reachable when the dialog is taller than a phone held
            // in landscape.
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    // heightIn, not height: a fixed 40dp row clips the Cancel/OK TextButtons'
                    // labels at large system font scales.
                    modifier = Modifier
                        .heightIn(min = 40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel
                    ) { Text(stringResource(Res.string.cancel_button)) }
                    TextButton(
                        onClick = onConfirm
                    ) { Text(stringResource(Res.string.ok_button)) }
                }
            }
        }
    }
}

/**
 * The clock-dial [TimePicker], or the keyboard [TimeInput] on short windows (a phone in
 * landscape), where the dial plus the dialog's title and buttons don't fit.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveTimePicker(
    state: TimePickerState,
    modifier: Modifier = Modifier,
    layout: WindowLayout = rememberWindowLayout(),
) {
    if (layout.isHeightCompact) {
        TimeInput(state = state, modifier = modifier)
    } else {
        TimePicker(state = state, modifier = modifier)
    }
}

/** [DisplayMode.Input] on short windows, where the calendar grid doesn't fit. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun adaptiveDatePickerDisplayMode(layout: WindowLayout = rememberWindowLayout()): DisplayMode =
    if (layout.isHeightCompact) DisplayMode.Input else DisplayMode.Picker

@Preview
@Composable
private fun TimePickerDialogPreview() {
    CarpoolTheme {
        TimePickerDialog(
            onCancel = {},
            onConfirm = {},
        ) {
            Text("14:30")
        }
    }
}
