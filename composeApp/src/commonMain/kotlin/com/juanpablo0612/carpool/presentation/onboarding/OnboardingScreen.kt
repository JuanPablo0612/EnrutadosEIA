package com.juanpablo0612.carpool.presentation.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.ContentWidth
import com.juanpablo0612.carpool.presentation.ui.theme.Elevation
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.ObserveAsEvents
import com.juanpablo0612.carpool.presentation.ui.util.ScreenInsets
import com.juanpablo0612.carpool.presentation.ui.util.ScreenPreviews
import com.juanpablo0612.carpool.presentation.ui.util.WindowLayout
import com.juanpablo0612.carpool.presentation.ui.util.centeredContent
import com.juanpablo0612.carpool.presentation.ui.util.rememberWindowLayout
import enrutadoseia.composeapp.generated.resources.Res
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
import enrutadoseia.composeapp.generated.resources.school_24px
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
    onAction: (OnboardingAction) -> Unit,
    layout: WindowLayout = rememberWindowLayout(),
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

    val isLastPage = state.currentPage == state.totalPages - 1

    Scaffold(contentWindowInsets = ScreenInsets, containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                horizontalArrangement = Arrangement.End
            ) {
                // Space is reserved (not collapsed via AnimatedVisibility) so the row doesn't
                // shift when Skip disappears on the last page.
                TextButton(
                    onClick = { onAction(OnboardingAction.OnSkip) },
                    enabled = !isLastPage,
                    modifier = Modifier.alpha(if (isLastPage) 0f else 1f)
                ) {
                    Text(stringResource(Res.string.onboarding_skip), style = MaterialTheme.typography.titleSmall)
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                OnboardingSlide(page = page, sideBySide = layout.prefersTwoPanes)
            }

            Column(
                modifier = Modifier
                    .centeredContent(ContentWidth.form)
                    .padding(
                        horizontal = Spacing.screenHorizontalForm,
                        // Short windows trim the space around the button to leave the slide room.
                        vertical = if (layout.isHeightCompact) Spacing.md else Spacing.xl,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (layout.isHeightCompact) Spacing.md else Spacing.xl),
            ) {
                PageIndicator(current = state.currentPage, total = state.totalPages)
                PrimaryButton(
                    text = stringResource(if (isLastPage) Res.string.onboarding_start else Res.string.onboarding_next),
                    onClick = {
                        onAction(if (isLastPage) OnboardingAction.OnFinish else OnboardingAction.OnNextPage)
                    },
                )
            }
        }
    }
}

/** Decorative: the slide's heading already tells screen-reader users where they are. */
@Composable
private fun PageIndicator(current: Int, total: Int) {
    Row(
        modifier = Modifier.clearAndSetSemantics {},
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(total) { index ->
            val width by animateDpAsState(if (index == current) 24.dp else 8.dp, label = "pageDot")
            Box(
                modifier = Modifier
                    // Pager-dot dimensions are component-intrinsic, not spacing.
                    .size(width = width, height = 8.dp)
                    .background(
                        color = if (index == current) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        },
                        shape = CircleShape
                    )
            )
        }
    }
}

/**
 * One slide: the illustration above the copy, or beside it when [sideBySide] (a phone in
 * landscape, a tablet), where stacking them would push the copy below the fold.
 */
@Composable
private fun OnboardingSlide(page: Int, sideBySide: Boolean) {
    val (title, body) = when (page) {
        0 -> stringResource(Res.string.onboarding_slide1_title) to stringResource(Res.string.onboarding_slide1_body)
        1 -> stringResource(Res.string.onboarding_slide2_title) to stringResource(Res.string.onboarding_slide2_body)
        else -> stringResource(Res.string.onboarding_slide3_title) to stringResource(Res.string.onboarding_slide3_body)
    }
    val badge: ImageVector = when (page) {
        0 -> vectorResource(Res.drawable.school_24px)
        1 -> vectorResource(Res.drawable.directions_car_24px)
        else -> vectorResource(Res.drawable.search_24px)
    }

    if (sideBySide) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .centeredContent(ContentWidth.list)
                .padding(horizontal = Spacing.screenHorizontalForm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xxl),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                RouteIllustration(
                    badge = badge,
                    modifier = Modifier
                        .widthIn(max = IllustrationMaxSize)
                        // Height first: the square takes the slide's height and no more.
                        .aspectRatio(1f, matchHeightConstraintsFirst = true),
                )
            }
            // Scrolls within the page so long copy at large font scales is never clipped.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                SlideCopy(title = title, body = body)
            }
        }
    } else {
        // Scrolls within the page so long copy at large font scales is never clipped.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .centeredContent(ContentWidth.form)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.screenHorizontalForm),
        ) {
            RouteIllustration(
                badge = badge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .widthIn(max = IllustrationMaxSize)
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            SlideCopy(title = title, body = body)
        }
    }
}

/** Past this the illustration stops reading as a picture and starts crowding out the copy. */
private val IllustrationMaxSize = 360.dp

@Composable
private fun SlideCopy(title: String, body: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.semantics { heading() },
    )

    Spacer(modifier = Modifier.height(Spacing.md))

    Text(
        text = body,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

/**
 * A stylised map: a street grid, a route from a hollow origin to a filled destination, and the
 * slide's [badge] riding on the route. Drawn rather than bundled as an image so it follows the
 * theme, dark mode included. Purely decorative.
 */
@Composable
private fun RouteIllustration(badge: ImageVector, modifier: Modifier = Modifier) {
    val ground = MaterialTheme.colorScheme.secondaryContainer
    val streets = MaterialTheme.colorScheme.surfaceContainerLowest
    val route = MaterialTheme.colorScheme.primary
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(ground)
            .drawBehind {
                val step = size.minDimension / 5f
                val streetWidth = 3.dp.toPx()
                var offset = step
                while (offset < size.maxDimension) {
                    drawLine(streets, Offset(offset, 0f), Offset(offset, size.height), streetWidth)
                    drawLine(streets, Offset(0f, offset), Offset(size.width, offset), streetWidth)
                    offset += step
                }
                val start = Offset(size.width * 0.18f, size.height * 0.82f)
                val end = Offset(size.width * 0.82f, size.height * 0.18f)
                val path = Path().apply {
                    moveTo(start.x, start.y)
                    cubicTo(
                        size.width * 0.35f, size.height * 0.62f,
                        size.width * 0.45f, size.height * 0.55f,
                        size.width * 0.5f, size.height * 0.5f,
                    )
                    cubicTo(
                        size.width * 0.6f, size.height * 0.42f,
                        size.width * 0.78f, size.height * 0.36f,
                        end.x, end.y,
                    )
                }
                drawPath(path, route, style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round))
                drawCircle(streets, radius = 12.dp.toPx(), center = start)
                drawCircle(route, radius = 12.dp.toPx(), center = start, style = Stroke(width = 5.dp.toPx()))
                drawCircle(route, radius = 13.dp.toPx(), center = end)
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(Elevation.raised, CircleShape)
                .background(streets, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = badge, contentDescription = null, tint = route, modifier = Modifier.size(32.dp))
        }
    }
}

@ScreenPreviews
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
