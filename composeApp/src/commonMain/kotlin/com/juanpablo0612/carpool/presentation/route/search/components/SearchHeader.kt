package com.juanpablo0612.carpool.presentation.route.search.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.CampusDirection
import com.juanpablo0612.carpool.presentation.place.campusShortName
import com.juanpablo0612.carpool.presentation.route.search.SearchRoutesAction
import com.juanpablo0612.carpool.presentation.route.search.SearchRoutesUiState
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.departureDayLabel
import com.juanpablo0612.carpool.presentation.ui.util.formatTime
import com.juanpablo0612.carpool.presentation.ui.util.rememberNowMs
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.cd_clear_place
import enrutadoseia.composeapp.generated.resources.close_24px
import enrutadoseia.composeapp.generated.resources.location_on_24px
import enrutadoseia.composeapp.generated.resources.passenger_home_title
import enrutadoseia.composeapp.generated.resources.schedule_24px
import enrutadoseia.composeapp.generated.resources.search_campus_label
import enrutadoseia.composeapp.generated.resources.search_direction_from_campus
import enrutadoseia.composeapp.generated.resources.search_direction_to_campus
import enrutadoseia.composeapp.generated.resources.search_place_label_from
import enrutadoseia.composeapp.generated.resources.search_place_label_to
import enrutadoseia.composeapp.generated.resources.search_place_placeholder_dropoff
import enrutadoseia.composeapp.generated.resources.search_place_placeholder_pickup
import enrutadoseia.composeapp.generated.resources.search_when_any
import enrutadoseia.composeapp.generated.resources.search_when_value
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

private val directions = listOf(CampusDirection.ToCampus, CampusDirection.FromCampus)

/**
 * The search form: which way (to or from the EIA), the passenger's place, the campus and when.
 * Every change re-runs the search, so there is no search button.
 */
@Composable
internal fun SearchHeader(
    state: SearchRoutesUiState,
    onAction: (SearchRoutesAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainerLowest, modifier = modifier) {
        Column {
            Column(
                modifier = Modifier.padding(
                    start = Spacing.screenHorizontal,
                    end = Spacing.screenHorizontal,
                    top = Spacing.lg,
                    bottom = Spacing.md,
                ),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Text(
                    text = stringResource(Res.string.passenger_home_title),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.semantics { heading() },
                )
                DirectionSelector(
                    selected = state.direction,
                    onSelected = { onAction(SearchRoutesAction.OnDirectionChanged(it)) },
                )
                PlaceField(
                    direction = state.direction,
                    place = state.place,
                    onPick = { onAction(SearchRoutesAction.OnPickPlace) },
                    onClear = { onAction(SearchRoutesAction.OnClearPlace) },
                )
                CampusAndTimeRow(
                    campus = state.campus,
                    selectedEpochMs = state.selectedEpochMs,
                    onCampusSelected = { onAction(SearchRoutesAction.OnCampusSelected(it)) },
                    onPickTime = { onAction(SearchRoutesAction.OnShowDateTimeSheet) },
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun DirectionSelector(selected: CampusDirection, onSelected: (CampusDirection) -> Unit) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        directions.forEachIndexed { index, direction ->
            SegmentedButton(
                selected = direction == selected,
                onClick = { onSelected(direction) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = directions.size),
                // The label alone carries the state; the default check icon would crowd it.
                icon = {},
            ) {
                Text(
                    text = stringResource(
                        when (direction) {
                            CampusDirection.ToCampus -> Res.string.search_direction_to_campus
                            CampusDirection.FromCampus -> Res.string.search_direction_from_campus
                        }
                    ),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
    }
}

/**
 * The passenger's end of the trip, shaped like a field but opening the place selector. Its
 * label follows the direction: where they get on going to campus, where they get off leaving it.
 */
@Composable
private fun PlaceField(
    direction: CampusDirection,
    place: Place?,
    onPick: () -> Unit,
    onClear: () -> Unit,
) {
    val label = stringResource(
        when (direction) {
            CampusDirection.ToCampus -> Res.string.search_place_label_from
            CampusDirection.FromCampus -> Res.string.search_place_label_to
        }
    )
    val placeholder = stringResource(
        when (direction) {
            CampusDirection.ToCampus -> Res.string.search_place_placeholder_pickup
            CampusDirection.FromCampus -> Res.string.search_place_placeholder_dropoff
        }
    )
    val value = place?.name
    Surface(
        onClick = onPick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        // The visible label and value are read together as the button's name.
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 56.dp)
                .padding(start = Spacing.lg, end = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.location_on_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Column(modifier = Modifier.weight(1f).padding(vertical = Spacing.sm)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = value ?: placeholder,
                    style = if (value != null) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
                    color = if (value != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (place != null) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.close_24px),
                        contentDescription = stringResource(Res.string.cd_clear_place),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun CampusAndTimeRow(
    campus: Place,
    selectedEpochMs: Long?,
    onCampusSelected: (Place) -> Unit,
    onPickTime: () -> Unit,
) {
    val now = rememberNowMs()
    val whenLabel = selectedEpochMs?.let {
        stringResource(Res.string.search_when_value, departureDayLabel(it, now), formatTime(it))
    } ?: stringResource(Res.string.search_when_any)
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = stringResource(Res.string.search_campus_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Place.campusPresets.forEach { preset ->
            FilterChip(
                selected = preset.id == campus.id,
                onClick = { onCampusSelected(preset) },
                label = { Text(preset.campusShortName()) },
            )
        }
        FilterChip(
            selected = selectedEpochMs != null,
            onClick = onPickTime,
            label = { Text(whenLabel) },
            leadingIcon = {
                Icon(
                    imageVector = vectorResource(Res.drawable.schedule_24px),
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                )
            },
        )
    }
}
