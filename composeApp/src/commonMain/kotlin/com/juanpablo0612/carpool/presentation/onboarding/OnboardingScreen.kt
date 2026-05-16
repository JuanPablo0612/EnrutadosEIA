package com.juanpablo0612.carpool.presentation.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.components.ObserveAsEvents
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.onboarding_next
import enrutadoseia.composeapp.generated.resources.onboarding_skip
import enrutadoseia.composeapp.generated.resources.onboarding_slide1_body
import enrutadoseia.composeapp.generated.resources.onboarding_slide1_title
import enrutadoseia.composeapp.generated.resources.onboarding_slide2_body
import enrutadoseia.composeapp.generated.resources.onboarding_slide2_title
import enrutadoseia.composeapp.generated.resources.onboarding_slide3_body
import enrutadoseia.composeapp.generated.resources.onboarding_slide3_title
import enrutadoseia.composeapp.generated.resources.onboarding_start
import org.jetbrains.compose.resources.stringResource

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onNavigateToApp: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            OnboardingEvent.NavigateToApp -> onNavigateToApp()
        }
    }

    OnboardingContent(state = state, onAction = viewModel::onAction)
}

@Composable
fun OnboardingContent(
    state: OnboardingUiState,
    onAction: (OnboardingAction) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = state.currentPage) { state.totalPages }

    LaunchedEffect(state.currentPage) {
        pagerState.animateScrollToPage(state.currentPage)
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                AnimatedVisibility(state.currentPage < state.totalPages - 1) {
                    TextButton(onClick = { onAction(OnboardingAction.OnSkip) }) {
                        Text(stringResource(Res.string.onboarding_skip))
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = false
            ) { page ->
                OnboardingSlide(page = page)
            }

            Row(
                modifier = Modifier.padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(state.totalPages) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == state.currentPage) 24.dp else 8.dp, 8.dp)
                            .background(
                                color = if (index == state.currentPage)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outlineVariant,
                                shape = CircleShape
                            )
                    )
                }
            }

            if (state.currentPage == state.totalPages - 1) {
                Button(
                    onClick = { onAction(OnboardingAction.OnFinish) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.onboarding_start))
                }
            } else {
                Button(
                    onClick = { onAction(OnboardingAction.OnNextPage) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.onboarding_next))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun OnboardingSlide(page: Int) {
    val (title, body) = when (page) {
        0 -> stringResource(Res.string.onboarding_slide1_title) to stringResource(Res.string.onboarding_slide1_body)
        1 -> stringResource(Res.string.onboarding_slide2_title) to stringResource(Res.string.onboarding_slide2_body)
        else -> stringResource(Res.string.onboarding_slide3_title) to stringResource(Res.string.onboarding_slide3_body)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(300.dp, 260.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.extraLarge
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = listOf("🚗", "🗺️", "🎯")[page],
                style = MaterialTheme.typography.displayLarge
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
