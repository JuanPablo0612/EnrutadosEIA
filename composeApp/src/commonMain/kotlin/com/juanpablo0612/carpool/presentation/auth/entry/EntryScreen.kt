package com.juanpablo0612.carpool.presentation.auth.entry

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.auth.components.EqualHeightColumn
import com.juanpablo0612.carpool.presentation.ui.components.AuthFormLayout
import com.juanpablo0612.carpool.presentation.ui.components.AuthHeader
import com.juanpablo0612.carpool.presentation.ui.components.AuthTopBar
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.ScreenPreviews
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.chevron_right_24px
import enrutadoseia.composeapp.generated.resources.entry_existing_subtitle
import enrutadoseia.composeapp.generated.resources.entry_existing_title
import enrutadoseia.composeapp.generated.resources.entry_new_subtitle
import enrutadoseia.composeapp.generated.resources.entry_new_title
import enrutadoseia.composeapp.generated.resources.entry_subtitle
import enrutadoseia.composeapp.generated.resources.entry_title
import enrutadoseia.composeapp.generated.resources.login_terms_footer
import enrutadoseia.composeapp.generated.resources.person_24px
import enrutadoseia.composeapp.generated.resources.person_add_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * Where signed-out users start: it asks whether they already have an account before showing any
 * form, because first-time users kept typing their email into the sign-in form.
 */
@Composable
fun EntryScreen(
    onCreateAccountClick: () -> Unit,
    onSignInClick: () -> Unit,
) {
    EntryContent(
        onCreateAccountClick = onCreateAccountClick,
        onSignInClick = onSignInClick,
    )
}

@Composable
fun EntryContent(
    onCreateAccountClick: () -> Unit,
    onSignInClick: () -> Unit,
) {
    AuthFormLayout(
        topBar = { AuthTopBar(onBackClick = {}, showBackButton = false) },
        footer = {
            Text(
                text = stringResource(Res.string.login_terms_footer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        },
    ) {
        AuthHeader(
            title = stringResource(Res.string.entry_title),
            subtitle = stringResource(Res.string.entry_subtitle),
        )

        Spacer(modifier = Modifier.height(Spacing.xxl))

        EqualHeightColumn(spacing = Spacing.md) {
            EntryChoiceCard(
                title = stringResource(Res.string.entry_new_title),
                subtitle = stringResource(Res.string.entry_new_subtitle),
                icon = vectorResource(Res.drawable.person_add_24px),
                onClick = onCreateAccountClick,
                emphasized = true,
            )
            EntryChoiceCard(
                title = stringResource(Res.string.entry_existing_title),
                subtitle = stringResource(Res.string.entry_existing_subtitle),
                icon = vectorResource(Res.drawable.person_24px),
                onClick = onSignInClick,
                emphasized = false,
            )
        }
    }
}

/**
 * One of the two answers. The [emphasized] card (creating an account) is filled so it reads as
 * the expected path; the other is outlined, but both share the same size and tap target.
 */
@Composable
private fun EntryChoiceCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    emphasized: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val containerColor = if (emphasized) colors.primary else colors.surfaceContainerLowest
    val titleColor = if (emphasized) colors.onPrimary else colors.onSurface
    val subtitleColor = if (emphasized) colors.onPrimary.copy(alpha = 0.85f) else colors.onSurfaceVariant
    val iconContainerColor = if (emphasized) colors.onPrimary.copy(alpha = 0.16f) else colors.primaryContainer
    val iconTint = if (emphasized) colors.onPrimary else colors.onPrimaryContainer

    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 104.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = containerColor,
        border = if (emphasized) null else BorderStroke(1.5.dp, colors.outline),
    ) {
        Row(
            modifier = Modifier.padding(start = Spacing.lg + Spacing.xs, end = Spacing.lg, top = Spacing.lg, bottom = Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            ChoiceIcon(icon = icon, containerColor = iconContainerColor, tint = iconTint)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Text(text = title, style = MaterialTheme.typography.titleLarge, color = titleColor)
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = subtitleColor)
            }
            Icon(
                imageVector = vectorResource(Res.drawable.chevron_right_24px),
                contentDescription = null,
                tint = if (emphasized) colors.onPrimary else colors.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun ChoiceIcon(icon: ImageVector, containerColor: Color, tint: Color) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .background(containerColor, MaterialTheme.shapes.large),
        contentAlignment = Alignment.Center,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint)
    }
}

@ScreenPreviews
@Composable
private fun EntryScreenPreview() {
    CarpoolTheme {
        EntryContent(onCreateAccountClick = {}, onSignInClick = {})
    }
}
