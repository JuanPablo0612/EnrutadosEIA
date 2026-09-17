package com.juanpablo0612.carpool.presentation.route.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.cancel
import enrutadoseia.composeapp.generated.resources.confirm
import enrutadoseia.composeapp.generated.resources.search_date_field_placeholder
import enrutadoseia.composeapp.generated.resources.search_date_placeholder
import enrutadoseia.composeapp.generated.resources.search_time_field_placeholder
import enrutadoseia.composeapp.generated.resources.search_tolerance_label
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

private enum class DateTimeSheetMode { MAIN, DATE, TIME }

// DatePickerDialog/BasicAlertDialog each open their own platform Dialog window. ModalBottomSheet
// is *also* backed by its own Dialog window (ModalBottomSheetDialog) — stacking a Dialog inside a
// Dialog is unreliable window-layering (the inner one can render behind, or have its touches
// swallowed by, the outer one). Rendering the pickers as inline sheet content instead of a nested
// Dialog sidesteps that entirely.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateTimeBottomSheet(
    currentEpochMs: Long?,
    currentTolerance: Int,
    sheetState: SheetState,
    onConfirm: (Long?, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val initialLocalDateTime = currentEpochMs?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault())
    }
    var selectedDate by remember { mutableStateOf(initialLocalDateTime?.date) }
    var selectedTime by remember { mutableStateOf(initialLocalDateTime?.time) }
    var selectedTolerance by remember { mutableIntStateOf(currentTolerance) }
    var mode by remember { mutableStateOf(DateTimeSheetMode.MAIN) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = (selectedDate ?: Clock.System.now().toLocalDateTime(TimeZone.UTC).date).let {
            LocalDateTime(it, LocalTime(0, 0)).toInstant(TimeZone.UTC).toEpochMilliseconds()
        }
    )
    val timePickerState = rememberTimePickerState(
        initialHour = selectedTime?.hour ?: 7,
        initialMinute = selectedTime?.minute ?: 0,
        is24Hour = false
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        when (mode) {
            DateTimeSheetMode.MAIN -> Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg)
                    .padding(bottom = Spacing.xl),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Text(
                    text = stringResource(Res.string.search_date_placeholder),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                OutlinedButton(
                    onClick = { mode = DateTimeSheetMode.DATE },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedDate?.let { formatDateOnly(it) } ?: stringResource(Res.string.search_date_field_placeholder))
                }

                OutlinedButton(
                    onClick = { mode = DateTimeSheetMode.TIME },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedTime?.let { formatTimeOnly(it) } ?: stringResource(Res.string.search_time_field_placeholder))
                }

                Text(
                    stringResource(Res.string.search_tolerance_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    listOf(15, 30, 60).forEach { tol ->
                        AssistChip(
                            onClick = { selectedTolerance = tol },
                            label = { Text("$tol min") },
                            colors = if (selectedTolerance == tol) {
                                AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            } else AssistChipDefaults.assistChipColors()
                        )
                    }
                }

                Button(
                    onClick = {
                        val date = selectedDate
                        val time = selectedTime
                        val epochMs = if (date != null && time != null) {
                            LocalDateTime(date, time).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                        } else null
                        onConfirm(epochMs, selectedTolerance)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.confirm))
                }
            }

            DateTimeSheetMode.DATE -> Column(modifier = Modifier.padding(bottom = Spacing.xl)) {
                DatePicker(state = datePickerState, modifier = Modifier.fillMaxWidth())
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { mode = DateTimeSheetMode.MAIN }) {
                        Text(stringResource(Res.string.cancel))
                    }
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { ms ->
                            selectedDate = Instant.fromEpochMilliseconds(ms).toLocalDateTime(TimeZone.UTC).date
                        }
                        mode = DateTimeSheetMode.MAIN
                    }) {
                        Text(stringResource(Res.string.confirm))
                    }
                }
            }

            DateTimeSheetMode.TIME -> Column(
                modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimePicker(state = timePickerState)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { mode = DateTimeSheetMode.MAIN }) {
                        Text(stringResource(Res.string.cancel))
                    }
                    TextButton(onClick = {
                        selectedTime = LocalTime(timePickerState.hour, timePickerState.minute)
                        mode = DateTimeSheetMode.MAIN
                    }) {
                        Text(stringResource(Res.string.confirm))
                    }
                }
            }
        }
    }
}

private fun formatDateOnly(date: LocalDate): String {
    @Suppress("DEPRECATION")
    val day = date.dayOfMonth.toString().padStart(2, '0')
    @Suppress("DEPRECATION")
    val month = date.monthNumber.toString().padStart(2, '0')
    return "$day/$month/${date.year}"
}

private fun formatTimeOnly(time: LocalTime): String {
    val hour = time.hour.toString().padStart(2, '0')
    val minute = time.minute.toString().padStart(2, '0')
    return "$hour:$minute"
}
