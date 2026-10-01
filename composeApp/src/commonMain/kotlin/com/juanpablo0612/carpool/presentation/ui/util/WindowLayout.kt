package com.juanpablo0612.carpool.presentation.ui.util

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isFinite
import androidx.compose.ui.unit.max
import androidx.window.core.layout.WindowSizeClass
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import kotlin.math.roundToInt

/**
 * The window's size buckets, read once per screen so layout decisions use the Material
 * breakpoints (600dp / 840dp wide, 480dp tall) instead of ad-hoc numbers.
 *
 * A phone in portrait is width-compact; the same phone in landscape is height-compact but not
 * width-compact; an unfolded foldable is medium width; a tablet in landscape is expanded.
 */
@Immutable
data class WindowLayout(
    val isWidthCompact: Boolean,
    val isWidthExpanded: Boolean,
    val isHeightCompact: Boolean,
) {
    /**
     * Wide and short enough that stacking content vertically wastes the screen: expanded
     * windows, and phones in landscape.
     */
    val prefersTwoPanes: Boolean get() = isWidthExpanded || (isHeightCompact && !isWidthCompact)

    companion object {
        /** A phone in portrait — the layout every screen was first designed for. */
        val PhonePortrait = WindowLayout(isWidthCompact = true, isWidthExpanded = false, isHeightCompact = false)
    }
}

@Composable
fun rememberWindowLayout(): WindowLayout {
    val sizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return remember(sizeClass) {
        WindowLayout(
            isWidthCompact = !sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND),
            isWidthExpanded = sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND),
            isHeightCompact = !sizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND),
        )
    }
}

/**
 * The insets a full screen must stay clear of: the system bars plus the display cutout. The
 * Material default is the system bars alone, which in landscape lets content slide under a
 * camera cutout on the side of the screen.
 */
val ScreenInsets: WindowInsets
    @Composable get() = WindowInsets.systemBars.union(WindowInsets.displayCutout)

/** The part of [ScreenInsets] a top app bar pads itself by. */
val TopBarInsets: WindowInsets
    @Composable get() = ScreenInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)

/**
 * The part of [ScreenInsets] a bar pinned to the bottom pads itself by — Scaffold's `bottomBar`
 * slot and sheet footers get no insets of their own.
 */
val BottomBarInsets: WindowInsets
    @Composable get() = ScreenInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)

/**
 * The horizontal padding that keeps content [maxWidth] wide and centred in [available] space,
 * never less than [gutter]. Unbounded space (inside a horizontal scroll) gets the plain gutter.
 */
fun centeredGutter(available: Dp, maxWidth: Dp, gutter: Dp): Dp {
    if (!available.isFinite) return gutter
    return max(gutter, (available - maxWidth) / 2)
}

/**
 * Lays out [content] with the horizontal padding from [centeredGutter], for lists. The list
 * applies it as `contentPadding` rather than being narrowed itself, so on a tablet it still
 * scrolls from the margins and its scroll edge stays at the window edge.
 */
@Composable
fun CenteredContent(
    contentMaxWidth: Dp,
    modifier: Modifier = Modifier,
    gutter: Dp = Spacing.screenHorizontal,
    content: @Composable BoxWithConstraintsScope.(horizontalPadding: Dp) -> Unit,
) {
    BoxWithConstraints(modifier = modifier) {
        content(centeredGutter(maxWidth, contentMaxWidth, gutter))
    }
}

/**
 * These padding values plus [margin] on the start and end, for a lazy list inside
 * [CenteredContent] whose items keep their own gutter: the margin centres them, the base
 * padding stays as it was on a phone.
 */
fun PaddingValues.plusHorizontal(margin: Dp): PaddingValues =
    if (margin == 0.dp) this else HorizontalMarginPadding(this, margin)

@Immutable
private class HorizontalMarginPadding(
    private val base: PaddingValues,
    private val margin: Dp,
) : PaddingValues {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        base.calculateLeftPadding(layoutDirection) + margin

    override fun calculateTopPadding(): Dp = base.calculateTopPadding()

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        base.calculateRightPadding(layoutDirection) + margin

    override fun calculateBottomPadding(): Dp = base.calculateBottomPadding()

    override fun equals(other: Any?): Boolean =
        other is HorizontalMarginPadding && other.base == base && other.margin == margin

    override fun hashCode(): Int = 31 * base.hashCode() + margin.hashCode()
}

/**
 * Centres a non-scrolling block (a form column, the inside of a sticky bottom bar) and caps its
 * width at [maxWidth].
 */
fun Modifier.centeredContent(maxWidth: Dp): Modifier =
    fillMaxWidth()
        .wrapContentWidth(Alignment.CenterHorizontally)
        .widthIn(max = maxWidth)

/**
 * Sizes a map or photo preview to the full available width at a 16:9 ratio, with its height
 * kept between [minHeight] and [maxHeight]: a fixed height reads as a thin strip on a tablet,
 * while a pure ratio would fill a phone's whole landscape height.
 */
fun Modifier.mediaPreviewSize(
    minHeight: Dp = 140.dp,
    maxHeight: Dp = 320.dp,
): Modifier = layout { measurable, constraints ->
    val width = if (constraints.hasBoundedWidth) constraints.maxWidth else (minHeight * 16f / 9f).roundToPx()
    val height = (width * 9f / 16f).roundToInt()
        .coerceIn(minHeight.roundToPx(), maxHeight.roundToPx())
        .coerceIn(constraints.minHeight, constraints.maxHeight)
    val placeable = measurable.measure(constraints.copy(minWidth = width, minHeight = height, maxHeight = height))
    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
}

/** Whether the software keyboard currently takes up part of the window. */
@Composable
fun isImeVisible(): Boolean = WindowInsets.ime.getBottom(LocalDensity.current) > 0

/** Grid span for headers, banners and messages that sit above or between a grid's cards. */
val FullLineSpan: LazyGridItemSpanScope.() -> GridItemSpan = { GridItemSpan(maxLineSpan) }
