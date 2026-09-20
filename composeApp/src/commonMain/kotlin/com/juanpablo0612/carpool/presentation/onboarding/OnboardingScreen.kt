package com.juanpablo0612.carpool.presentation.onboarding

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.add_road_24px
import enrutadoseia.composeapp.generated.resources.directions_car_24px
import enrutadoseia.composeapp.generated.resources.onboarding_next
import enrutadoseia.composeapp.generated.resources.onboarding_skip
import enrutadoseia.composeapp.generated.resources.onboarding_slide1_body
import enrutadoseia.composeapp.generated.resources.onboarding_slide1_title
import enrutadoseia.composeapp.generated.resources.onboarding_slide2_body
import enrutadoseia.composeapp.generated.resources.onboarding_slide2_title
import enrutadoseia.composeapp.generated.resources.onboarding_slide3_body
import enrutadoseia.composeapp.generated.resources.onboarding_slide3_title
import enrutadoseia.composeapp.generated.resources.onboarding_start
import enrutadoseia.composeapp.generated.resources.search_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

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

    // Two-way sync: ViewModel-driven page changes (Next/Skip) animate the pager, and user
    // swipes on the pager report back so the dot indicator / Skip visibility / final-page
    // button stay in sync with what's actually on screen.
    LaunchedEffect(state.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (page != state.currentPage) {
                onAction(OnboardingAction.OnPageChanged(page))
            }
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Spacing.screenHorizontalForm),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // Space is reserved (not collapsed via AnimatedVisibility) so the row doesn't
                // shift when Skip disappears on the last page.
                val showSkip = state.currentPage < state.totalPages - 1
                TextButton(
                    onClick = { onAction(OnboardingAction.OnSkip) },
                    enabled = showSkip,
                    modifier = Modifier.alpha(if (showSkip) 1f else 0f)
                ) {
                    Text(stringResource(Res.string.onboarding_skip))
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                OnboardingSlide(page = page)
            }

            Row(
                modifier = Modifier.padding(vertical = Spacing.xl),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                repeat(state.totalPages) { index ->
                    Box(
                        modifier = Modifier
                            // Pager-dot dimensions are component-intrinsic, not spacing.
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

            Spacer(modifier = Modifier.height(Spacing.lg))
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
    val icon: ImageVector = when (page) {
        0 -> vectorResource(Res.drawable.directions_car_24px)
        1 -> vectorResource(Res.drawable.add_road_24px)
        else -> vectorResource(Res.drawable.search_24px)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            // Illustration placeholder dimensions are component-intrinsic, not spacing.
            modifier = Modifier
                .size(300.dp, 260.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.extraLarge
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                // Component-intrinsic illustration icon size, not a spacing step.
                modifier = Modifier.size(96.dp)
            )
        }

        // 40.dp falls between the xl(24)/xxl(32) steps; kept literal rather than nudging this
        // gap onto the nearest token and altering the onboarding illustration's layout.
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun OnboardingFirstPagePreview() {
    CarpoolTheme {
        OnboardingContent(
            state = OnboardingUiState(currentPage = 0, totalPages = 3),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun OnboardingLastPagePreview() {
    CarpoolTheme {
        OnboardingContent(
            state = OnboardingUiState(currentPage = 2, totalPages = 3),
            onAction = {}
        )
    }
}
