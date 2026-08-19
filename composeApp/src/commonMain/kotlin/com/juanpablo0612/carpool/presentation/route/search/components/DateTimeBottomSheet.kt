package com.juanpablo0612.carpool.presentation.route.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.search_button
import enrutadoseia.composeapp.generated.resources.search_date_placeholder
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateTimeBottomSheet(
    currentEpochMs: Long?,
    currentTolerance: Int,
    sheetState: SheetState,
    onConfirm: (Long?, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTolerance by remember { mutableIntStateOf(currentTolerance) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
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

            Text(
                "Tolerancia",
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
                onClick = { onConfirm(currentEpochMs, selectedTolerance) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.search_button))
            }
        }
    }
}
