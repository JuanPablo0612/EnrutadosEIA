package com.juanpablo0612.carpool.presentation.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juanpablo0612.carpool.domain.auth.model.UserRole
import com.juanpablo0612.carpool.presentation.ui.components.ActionButton
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.home_create_route_cta
import enrutadoseia.composeapp.generated.resources.home_driver_empty_subtitle
import enrutadoseia.composeapp.generated.resources.home_driver_empty_title
import enrutadoseia.composeapp.generated.resources.home_passenger_empty_subtitle
import enrutadoseia.composeapp.generated.resources.home_passenger_empty_title
import enrutadoseia.composeapp.generated.resources.home_register_vehicle_cta
import enrutadoseia.composeapp.generated.resources.home_search_trip_cta
import enrutadoseia.composeapp.generated.resources.search_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun HomeDashboardEmpty(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
) {
    if (state.role == UserRole.Driver) {
        EmptyState(
            icon = vectorResource(Res.drawable.directions_car_24px),
            title = stringResource(Res.string.home_driver_empty_title),
            description = stringResource(Res.string.home_driver_empty_subtitle),
            primaryAction = ActionButton(
                label = stringResource(Res.string.home_register_vehicle_cta),
                onClick = { onAction(HomeAction.RegisterVehicle) },
            ),
            secondaryAction = ActionButton(
                label = stringResource(Res.string.home_create_route_cta),
                onClick = { onAction(HomeAction.CreateRoute) },
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.xl),
        )
    } else {
        EmptyState(
            icon = vectorResource(Res.drawable.search_24px),
            title = stringResource(Res.string.home_passenger_empty_title),
            description = stringResource(Res.string.home_passenger_empty_subtitle),
            primaryAction = ActionButton(
                label = stringResource(Res.string.home_search_trip_cta),
                onClick = { onAction(HomeAction.SearchTrips) },
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.xl),
        )
    }
}
