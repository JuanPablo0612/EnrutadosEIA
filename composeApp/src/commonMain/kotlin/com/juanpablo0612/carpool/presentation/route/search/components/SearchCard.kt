package com.juanpablo0612.carpool.presentation.route.search.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.juanpablo0612.carpool.presentation.route.search.SearchRoutesAction
import com.juanpablo0612.carpool.presentation.route.search.SearchRoutesUiState
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.cd_swap_origin_destination
import enrutadoseia.composeapp.generated.resources.filter_list_24px
import enrutadoseia.composeapp.generated.resources.search_button
import enrutadoseia.composeapp.generated.resources.search_date_placeholder
import enrutadoseia.composeapp.generated.resources.search_destination_placeholder
import enrutadoseia.composeapp.generated.resources.search_filters_button
import enrutadoseia.composeapp.generated.resources.search_origin_placeholder
import enrutadoseia.composeapp.generated.resources.swap_horiz_24px
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun SearchCard(
    state: SearchRoutesUiState,
    onAction: (SearchRoutesAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.origin?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text(stringResource(Res.string.search_origin_placeholder)) },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onAction(SearchRoutesAction.OnPickOrigin) }
                        .semantics { role = Role.Button },
                    singleLine = true
                )
                IconButton(onClick = { onAction(SearchRoutesAction.OnSwapPlaces) }) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.swap_horiz_24px),
                        contentDescription = stringResource(Res.string.cd_swap_origin_destination),
                        modifier = Modifier.rotate(90f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            OutlinedTextField(
                value = state.destination?.name ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(stringResource(Res.string.search_destination_placeholder)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(SearchRoutesAction.OnPickDestination) }
                    .semantics { role = Role.Button },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            OutlinedTextField(
                value = if (state.selectedEpochMs != null) formatEpochShort(state.selectedEpochMs) else "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(stringResource(Res.string.search_date_placeholder)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(SearchRoutesAction.OnShowDateTimeSheet) }
                    .semantics { role = Role.Button },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = { onAction(SearchRoutesAction.OnShowFilters) },
                    label = { Text(stringResource(Res.string.search_filters_button)) },
                    leadingIcon = {
                        Icon(
                            imageVector = vectorResource(Res.drawable.filter_list_24px),
                            contentDescription = null
                        )
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { onAction(SearchRoutesAction.OnSearchClick) },
                    enabled = !state.isSearching
                ) {
                    Text(stringResource(Res.string.search_button))
                }
            }
        }
    }
}

internal fun formatEpochShort(epochMs: Long): String {
    val instant = Instant.fromEpochMilliseconds(epochMs)
    val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = local.hour.toString().padStart(2, '0')
    val minute = local.minute.toString().padStart(2, '0')
    @Suppress("DEPRECATION")
    val day = local.dayOfMonth.toString().padStart(2, '0')
    @Suppress("DEPRECATION")
    val month = local.monthNumber.toString().padStart(2, '0')
    return "$day/$month · $hour:$minute"
}
