package com.juanpablo0612.carpool.presentation.routes.create.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.routes.model.RouteType
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.route_type_from_uni
import enrutadoseia.composeapp.generated.resources.route_type_to_uni
import org.jetbrains.compose.resources.stringResource

@Composable
fun RouteTypeToggle(
    selectedType: RouteType,
    onTypeChange: (RouteType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        FilterChip(
            selected = selectedType is RouteType.ToUniversity,
            onClick = { onTypeChange(RouteType.ToUniversity) },
            label = { Text(stringResource(Res.string.route_type_to_uni)) },
            modifier = Modifier.weight(1f).padding(end = 4.dp)
        )
        FilterChip(
            selected = selectedType is RouteType.FromUniversity,
            onClick = { onTypeChange(RouteType.FromUniversity) },
            label = { Text(stringResource(Res.string.route_type_from_uni)) },
            modifier = Modifier.weight(1f).padding(start = 4.dp)
        )
    }
}
