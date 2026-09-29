package com.juanpablo0612.carpool.presentation.place

import androidx.compose.runtime.Composable
import com.juanpablo0612.carpool.domain.place.model.Place
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.campus_short_las_palmas
import enrutadoseia.composeapp.generated.resources.campus_short_zuniga
import org.jetbrains.compose.resources.stringResource

/** "Las Palmas" / "Zúñiga" for a campus preset, for chips where the full name won't fit. */
@Composable
fun Place.campusShortName(): String = when (id) {
    Place.EIA_LAS_PALMAS.id -> stringResource(Res.string.campus_short_las_palmas)
    Place.EIA_ZUNIGA.id -> stringResource(Res.string.campus_short_zuniga)
    else -> name
}
