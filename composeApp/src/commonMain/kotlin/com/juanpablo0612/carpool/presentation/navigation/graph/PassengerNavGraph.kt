package com.juanpablo0612.carpool.presentation.navigation.graph

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.juanpablo0612.carpool.presentation.navigation.Route
import com.juanpablo0612.carpool.presentation.routes.search.SearchRoutesScreen
import com.juanpablo0612.carpool.presentation.routes.search.SearchRoutesViewModel
import com.juanpablo0612.carpool.presentation.session.UserSession
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.passengerNavGraph(
    onSwitchRole: () -> Unit,
    onLogout: () -> Unit
) {
    composable<Route.PassengerHome> {
        val userSession: UserSession = koinInject()
        val user by userSession.user.collectAsState()
        val isDualRole = user?.let { it.isDriver && it.isPassenger } ?: false
        user?.let { u ->
            val viewModel: SearchRoutesViewModel = koinViewModel()
            SearchRoutesScreen(
                viewModel = viewModel,
                user = u,
                isDualRole = isDualRole,
                onSwitchRole = onSwitchRole,
                onLogout = onLogout
            )
        }
    }
}
