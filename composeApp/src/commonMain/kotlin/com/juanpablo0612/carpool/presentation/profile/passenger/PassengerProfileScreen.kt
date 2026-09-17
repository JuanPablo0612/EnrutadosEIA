package com.juanpablo0612.carpool.presentation.profile.passenger

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.auth.model.PublicProfile
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolBackTopBar
import com.juanpablo0612.carpool.presentation.ui.components.DetailSkeleton
import com.juanpablo0612.carpool.presentation.ui.components.UserAvatar
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.profile_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun PassengerProfileScreen(
    viewModel: PassengerProfileViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    PassengerProfileContent(state = state, onBackClick = onBackClick)
}

@Composable
fun PassengerProfileContent(
    state: PassengerProfileUiState,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CarpoolBackTopBar(
                title = state.profile?.name ?: stringResource(Res.string.profile_title),
                onBack = onBackClick
            )
        }
    ) { padding ->
        when {
            state.isLoading -> DetailSkeleton(modifier = Modifier.fillMaxSize().padding(padding))
            else -> {
                val profile = state.profile
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (profile != null) {
                        UserAvatar(name = profile.name, photoUrl = profile.photoUrl, size = 96.dp)
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        profile.bio?.takeIf { it.isNotBlank() }?.let { bio ->
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Text(
                                text = bio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PassengerProfileContentPreview() {
    CarpoolTheme {
        PassengerProfileContent(
            state = PassengerProfileUiState(
                isLoading = false,
                profile = PublicProfile(id = "p1", name = "Ana Torres", bio = "Estudiante de Ingeniería")
            ),
            onBackClick = {}
        )
    }
}
