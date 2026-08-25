package com.juanpablo0612.carpool.presentation.route.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.juanpablo0612.carpool.presentation.route.search.SearchFilters
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.search_button
import enrutadoseia.composeapp.generated.resources.search_filter_female_driver
import enrutadoseia.composeapp.generated.resources.search_filter_female_driver_coming_soon
import enrutadoseia.composeapp.generated.resources.search_filter_max_contribution
import enrutadoseia.composeapp.generated.resources.search_filters_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FiltersBottomSheet(
    filters: SearchFilters,
    sheetState: SheetState,
    onApply: (SearchFilters) -> Unit,
    onDismiss: () -> Unit
) {
    var maxContrib by remember { mutableStateOf(filters.maxContribution?.toString() ?: "") }

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
                text = stringResource(Res.string.search_filters_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            OutlinedTextField(
                value = maxContrib,
                onValueChange = { maxContrib = it.filter { c -> c.isDigit() } },
                label = { Text(stringResource(Res.string.search_filter_max_contribution)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                prefix = { Text("$") }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(stringResource(Res.string.search_filter_female_driver), style = MaterialTheme.typography.bodyMedium)
                    Text(
                        stringResource(Res.string.search_filter_female_driver_coming_soon),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = false, onCheckedChange = {}, enabled = false)
            }

            Button(
                onClick = {
                    onApply(
                        SearchFilters(
                            maxContribution = maxContrib.toIntOrNull()
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.search_button))
            }
        }
    }
}
