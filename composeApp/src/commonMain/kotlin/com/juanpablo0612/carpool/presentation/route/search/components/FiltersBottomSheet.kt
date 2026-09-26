package com.juanpablo0612.carpool.presentation.route.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.juanpablo0612.carpool.domain.trip.model.TripSearchCriteria
import com.juanpablo0612.carpool.presentation.route.search.SearchFilters
import com.juanpablo0612.carpool.presentation.route.search.formatDistance
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.confirm
import enrutadoseia.composeapp.generated.resources.search_filter_max_contribution
import enrutadoseia.composeapp.generated.resources.search_filter_max_walk
import enrutadoseia.composeapp.generated.resources.search_filter_max_walk_hint
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
    var maxWalk by remember { mutableIntStateOf(filters.maxWalkMeters) }

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

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = stringResource(Res.string.search_filter_max_walk),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = stringResource(Res.string.search_filter_max_walk_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    TripSearchCriteria.WALK_RADIUS_OPTIONS_METERS.forEach { option ->
                        FilterChip(
                            selected = option == maxWalk,
                            onClick = { maxWalk = option },
                            label = { Text(formatDistance(option.toDouble())) }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = maxContrib,
                onValueChange = { maxContrib = it.filter { c -> c.isDigit() } },
                label = { Text(stringResource(Res.string.search_filter_max_contribution)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                prefix = { Text("$") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
            )

            Button(
                onClick = {
                    onApply(
                        SearchFilters(
                            maxContribution = maxContrib.toIntOrNull(),
                            maxWalkMeters = maxWalk,
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.confirm))
            }
        }
    }
}
