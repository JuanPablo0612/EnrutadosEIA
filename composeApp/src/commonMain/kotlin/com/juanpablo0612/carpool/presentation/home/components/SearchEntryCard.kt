package com.juanpablo0612.carpool.presentation.home.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.CampusDirection
import com.juanpablo0612.carpool.presentation.place.campusShortName
import com.juanpablo0612.carpool.presentation.route.search.SearchShortcut
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.home_search_prompt
import enrutadoseia.composeapp.generated.resources.search_24px
import enrutadoseia.composeapp.generated.resources.search_preset_from_campus
import enrutadoseia.composeapp.generated.resources.search_preset_to_campus
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/** Every campus in both directions, "to" first since most trips go to class. */
private val shortcuts: List<SearchShortcut> =
    Place.campusPresets.map { SearchShortcut(it, CampusDirection.ToCampus) } +
        Place.campusPresets.map { SearchShortcut(it, CampusDirection.FromCampus) }

/**
 * The way into Buscar from Inicio: a button shaped like a search field, plus one-tap campus
 * searches. The field is a button, not an input: a search needs a place and a campus, which the
 * Buscar tab collects.
 */
@Composable
fun SearchEntryCard(
    onSearch: () -> Unit,
    onShortcut: (SearchShortcut) -> Unit,
    modifier: Modifier = Modifier,
) {
    CarpoolListCard(modifier = modifier, contentPadding = PaddingValues(Spacing.md)) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Surface(
                onClick = onSearch,
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.search_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(Res.string.home_search_prompt),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                shortcuts.forEach { shortcut ->
                    SuggestionChip(
                        onClick = { onShortcut(shortcut) },
                        label = { Text(shortcut.label()) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchShortcut.label(): String = stringResource(
    when (direction) {
        CampusDirection.ToCampus -> Res.string.search_preset_to_campus
        CampusDirection.FromCampus -> Res.string.search_preset_from_campus
    },
    campus.campusShortName(),
)
