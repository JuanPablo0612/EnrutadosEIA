package com.juanpablo0612.carpool.presentation.route.search.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.campus_short_las_palmas
import enrutadoseia.composeapp.generated.resources.campus_short_zuniga
import enrutadoseia.composeapp.generated.resources.search_preset_from_campus
import enrutadoseia.composeapp.generated.resources.search_preset_to_campus
import org.jetbrains.compose.resources.stringResource

/**
 * One-tap chips to search to or from a campus, since nearly every trip starts or ends at one.
 * Tapping a selected chip clears that field.
 */
@Composable
internal fun CampusQuickPicks(
    origin: Place?,
    destination: Place?,
    onCampusSelected: (campus: Place, asOrigin: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Place.campusPresets.forEach { campus ->
            FilterChip(
                selected = destination?.id == campus.id,
                onClick = { onCampusSelected(campus, false) },
                label = { Text(stringResource(Res.string.search_preset_to_campus, campus.shortName())) },
            )
        }
        Place.campusPresets.forEach { campus ->
            FilterChip(
                selected = origin?.id == campus.id,
                onClick = { onCampusSelected(campus, true) },
                label = { Text(stringResource(Res.string.search_preset_from_campus, campus.shortName())) },
            )
        }
    }
}

@Composable
private fun Place.shortName(): String = when (id) {
    Place.EIA_LAS_PALMAS.id -> stringResource(Res.string.campus_short_las_palmas)
    Place.EIA_ZUNIGA.id -> stringResource(Res.string.campus_short_zuniga)
    else -> name
}

@Preview
@Composable
private fun CampusQuickPicksPreview() {
    CarpoolTheme {
        CampusQuickPicks(origin = null, destination = Place.EIA_LAS_PALMAS, onCampusSelected = { _, _ -> })
    }
}
