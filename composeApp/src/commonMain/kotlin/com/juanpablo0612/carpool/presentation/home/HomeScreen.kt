package com.juanpablo0612.carpool.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import enrutadoseia.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    user: User,
    isDualRole: Boolean,
    onCreateRouteClick: () -> Unit,
    onSwitchRole: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(Res.string.driver_home_title),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = user.name ?: user.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    if (isDualRole) {
                        IconButton(onClick = onSwitchRole) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.arrow_forward_24px),
                                contentDescription = stringResource(Res.string.switch_role)
                            )
                        }
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_back_24px),
                            contentDescription = stringResource(Res.string.logout_title)
                        )
                    }
                }
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
            onLogout = {}
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
            onLogout = {}
        )
    }
}
