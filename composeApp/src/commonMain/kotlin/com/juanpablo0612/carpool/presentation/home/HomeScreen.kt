package com.juanpablo0612.carpool.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTopBar
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_24px
import enrutadoseia.composeapp.generated.resources.driver_home_title
import enrutadoseia.composeapp.generated.resources.driver_home_welcome
import enrutadoseia.composeapp.generated.resources.nav_create_route
import enrutadoseia.composeapp.generated.resources.role_selector_driver_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun HomeScreen(
    user: User,
    isDualRole: Boolean,
    onCreateRouteClick: () -> Unit,
    onSwitchRole: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Scaffold(
        topBar = {
            CarpoolTopBar(
                title = stringResource(Res.string.driver_home_title),
                user = user,
                isDualRole = isDualRole,
                currentRoleLabel = stringResource(Res.string.role_selector_driver_title),
                onAvatarClick = onNavigateToProfile,
                onRoleToggle = if (isDualRole) onSwitchRole else null
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateRouteClick) {
                Icon(
                    imageVector = vectorResource(Res.drawable.add_24px),
                    contentDescription = stringResource(Res.string.nav_create_route)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(Res.string.driver_home_welcome, user.name ?: ""),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    CarpoolTheme {
        HomeScreen(
            user = User(
                id = "1",
                email = "conductor@eia.edu.co",
                name = "Carlos López",
                isEmailVerified = true,
                isPassenger = false,
                isDriver = true
            ),
            isDualRole = false,
            onCreateRouteClick = {},
            onSwitchRole = {},
            onNavigateToProfile = {}
        )
    }
}

@Preview
@Composable
private fun HomeScreenDualRolePreview() {
    CarpoolTheme {
        HomeScreen(
            user = User(
                id = "2",
                email = "dual@eia.edu.co",
                name = "Ana Martínez",
                isEmailVerified = true,
                isPassenger = true,
                isDriver = true
            ),
            isDualRole = true,
            onCreateRouteClick = {},
            onSwitchRole = {},
            onNavigateToProfile = {}
        )
    }
}
