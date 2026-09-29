package com.juanpablo0612.carpool.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.bricolage_grotesque_bold
import enrutadoseia.composeapp.generated.resources.bricolage_grotesque_semibold
import enrutadoseia.composeapp.generated.resources.figtree_bold
import enrutadoseia.composeapp.generated.resources.figtree_medium
import enrutadoseia.composeapp.generated.resources.figtree_regular
import enrutadoseia.composeapp.generated.resources.figtree_semibold
import org.jetbrains.compose.resources.Font

/** Display face: headlines and screen titles. Only the weights the scale uses are bundled. */
@Composable
private fun displayFontFamily(): FontFamily = FontFamily(
    Font(Res.font.bricolage_grotesque_semibold, FontWeight.SemiBold),
    Font(Res.font.bricolage_grotesque_bold, FontWeight.Bold),
)

/** Text face: everything that is read rather than scanned — body, labels, buttons. */
@Composable
private fun textFontFamily(): FontFamily = FontFamily(
    Font(Res.font.figtree_regular, FontWeight.Normal),
    Font(Res.font.figtree_medium, FontWeight.Medium),
    Font(Res.font.figtree_semibold, FontWeight.SemiBold),
    Font(Res.font.figtree_bold, FontWeight.Bold),
)

/**
 * The app's type scale: Bricolage Grotesque for display and headline roles, Figtree for the rest.
 *
 * Every role is defined here, with its line height, because each one sets a font family: a role
 * left to the Material default would silently fall back to the platform font.
 *
 * It is a composable because Compose Multiplatform resolves font resources in composition.
 *
 * Canonical usage:
 *  | Onboarding / auth hero title     | displaySmall   |
 *  | Home greeting, big state titles  | headlineMedium |
 *  | Empty / error state title        | headlineSmall  |
 *  | Screen title (top bar)           | titleLarge     |
 *  | Card title, section header       | titleMedium    |
 *  | Emphasised inline value          | titleSmall     |
 *  | Body copy                        | bodyLarge      |
 *  | Card body                        | bodyMedium     |
 *  | Card secondary metadata          | bodySmall      |
 *  | Button label                     | labelLarge     |
 *  | Badge / chip / navigation label  | labelMedium    |
 */
@Composable
internal fun appTypography(): Typography {
    val display = displayFontFamily()
    val text = textFontFamily()
    return Typography(
        displayLarge = display.style(FontWeight.Bold, size = 48, lineHeight = 54, DisplayTracking),
        displayMedium = display.style(FontWeight.Bold, size = 40, lineHeight = 46, DisplayTracking),
        displaySmall = display.style(FontWeight.Bold, size = 32, lineHeight = 38, DisplayTracking),
        headlineLarge = display.style(FontWeight.Bold, size = 30, lineHeight = 36, DisplayTracking),
        headlineMedium = display.style(FontWeight.Bold, size = 28, lineHeight = 34, DisplayTracking),
        headlineSmall = display.style(FontWeight.Bold, size = 24, lineHeight = 30, DisplayTracking),
        titleLarge = display.style(FontWeight.Bold, size = 22, lineHeight = 28, DisplayTracking),
        titleMedium = text.style(FontWeight.SemiBold, size = 16, lineHeight = 22),
        titleSmall = text.style(FontWeight.SemiBold, size = 14, lineHeight = 20),
        bodyLarge = text.style(FontWeight.Normal, size = 16, lineHeight = 24),
        bodyMedium = text.style(FontWeight.Normal, size = 14, lineHeight = 20),
        bodySmall = text.style(FontWeight.Normal, size = 13, lineHeight = 18),
        labelLarge = text.style(FontWeight.SemiBold, size = 15, lineHeight = 20),
        labelMedium = text.style(FontWeight.SemiBold, size = 12, lineHeight = 16),
        labelSmall = text.style(FontWeight.Medium, size = 11, lineHeight = 16),
    )
}

/**
 * The display face is drawn loose; a slight negative tracking tightens it at headline sizes.
 * The text face is already spaced for reading and keeps its default tracking.
 */
private val DisplayTracking = (-0.01).em

private fun FontFamily.style(
    weight: FontWeight,
    size: Int,
    lineHeight: Int,
    tracking: TextUnit = TextUnit.Unspecified,
) = TextStyle(
    fontFamily = this,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = tracking,
)
