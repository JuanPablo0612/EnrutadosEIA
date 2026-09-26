package com.juanpablo0612.carpool.presentation.route.search.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.route.search.SearchRoutesAction
import com.juanpablo0612.carpool.presentation.route.search.SearchRoutesUiState
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.formatDayMonthTime
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.cd_clear_date
import enrutadoseia.composeapp.generated.resources.cd_clear_destination
import enrutadoseia.composeapp.generated.resources.cd_clear_origin
import enrutadoseia.composeapp.generated.resources.cd_search_field
import enrutadoseia.composeapp.generated.resources.cd_swap_origin_destination
import enrutadoseia.composeapp.generated.resources.close_24px
import enrutadoseia.composeapp.generated.resources.filter_list_24px
import enrutadoseia.composeapp.generated.resources.search_button
import enrutadoseia.composeapp.generated.resources.search_date_placeholder
import enrutadoseia.composeapp.generated.resources.search_destination_placeholder
import enrutadoseia.composeapp.generated.resources.search_filters_button
import enrutadoseia.composeapp.generated.resources.search_origin_placeholder
import enrutadoseia.composeapp.generated.resources.swap_horiz_24px
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
                    clearDescription = stringResource(Res.string.cd_clear_origin),
                    onClear = { onAction(SearchRoutesAction.OnClearOrigin) },
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
                clearDescription = stringResource(Res.string.cd_clear_destination),
                onClear = { onAction(SearchRoutesAction.OnClearDestination) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            CampusQuickPicks(
                origin = state.origin,
                destination = state.destination,
                onCampusSelected = { campus, asOrigin ->
                    onAction(SearchRoutesAction.OnCampusPreset(campus, asOrigin))
                }
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            PickerField(
                value = if (state.selectedEpochMs != null) formatDayMonthTime(state.selectedEpochMs) else "",
                placeholder = stringResource(Res.string.search_date_placeholder),
                onClick = { onAction(SearchRoutesAction.OnShowDateTimeSheet) },
                clearDescription = stringResource(Res.string.cd_clear_date),
                onClear = { onAction(SearchRoutesAction.OnDateTimeChanged(null, state.toleranceMinutes)) },
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
                    enabled = !state.isSearching && !state.isLoading
                ) {
                    Text(stringResource(Res.string.search_button))
                }
            }
        }
    }
}

// A plain clickable surface styled to look like an outlined field, not a read-only
// OutlinedTextField: a readOnly text field consumes the tap for focus before it reaches a
// `.clickable` on the same node, so it would highlight and never open the picker. The clear
// button is a separate node so TalkBack announces it on its own.
@Composable
private fun PickerField(
    value: String,
    placeholder: String,
    onClick: () -> Unit,
    clearDescription: String,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fieldDescription = if (value.isBlank()) placeholder else stringResource(Res.string.cd_search_field, placeholder, value)
    Row(
        modifier = modifier
            .height(56.dp) // matches OutlinedTextField's default single-line height
            .clip(RoundedCornerShape(4.dp))
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                RoundedCornerShape(4.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable(onClick = onClick)
                .semantics {
                    role = Role.Button
                    contentDescription = fieldDescription
                },
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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = Spacing.md)
            )
        }
        if (value.isNotBlank()) {
            IconButton(onClick = onClear) {
                Icon(
                    imageVector = vectorResource(Res.drawable.close_24px),
                    contentDescription = clearDescription
                )
            }
        }
    }
}
