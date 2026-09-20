package com.juanpablo0612.carpool.presentation.route.search.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
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
                PickerField(
                    value = state.origin?.name ?: "",
                    placeholder = stringResource(Res.string.search_origin_placeholder),
                    onClick = { onAction(SearchRoutesAction.OnPickOrigin) },
                    modifier = Modifier.weight(1f)
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

            PickerField(
                value = state.destination?.name ?: "",
                placeholder = stringResource(Res.string.search_destination_placeholder),
                onClick = { onAction(SearchRoutesAction.OnPickDestination) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            PickerField(
                value = if (state.selectedEpochMs != null) formatEpochShort(state.selectedEpochMs) else "",
                placeholder = stringResource(Res.string.search_date_placeholder),
                onClick = { onAction(SearchRoutesAction.OnShowDateTimeSheet) },
                modifier = Modifier.fillMaxWidth()
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

// A read-only OutlinedTextField plus a transparent tap-catching overlay used to be the pattern
// here — a readOnly text field still consumes the tap for focus before it ever reaches a
// `.clickable` on the same node, so the field just highlighted and never opened the picker. A
// plain clickable surface styled to look like an outlined field sidesteps the problem entirely
// instead of working around it with an overlay.
@Composable
private fun PickerField(
    value: String,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(56.dp) // matches OutlinedTextField's default single-line height
            .clip(RoundedCornerShape(4.dp))
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
            .semantics { role = Role.Button },
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = value.ifBlank { placeholder },
            style = MaterialTheme.typography.bodyLarge,
            color = if (value.isBlank()) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.padding(horizontal = Spacing.md)
        )
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
