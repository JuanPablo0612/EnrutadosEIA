package com.juanpablo0612.carpool.presentation.auth.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.auth.components.EqualHeightColumn
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolListCard
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.chevron_right_24px
import enrutadoseia.composeapp.generated.resources.lock_24px
import enrutadoseia.composeapp.generated.resources.login_help_forgot_subtitle
import enrutadoseia.composeapp.generated.resources.login_help_forgot_title
import enrutadoseia.composeapp.generated.resources.login_help_register_subtitle
import enrutadoseia.composeapp.generated.resources.login_help_register_title
import enrutadoseia.composeapp.generated.resources.login_help_title
import enrutadoseia.composeapp.generated.resources.person_add_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * Shown after a rejected sign-in. The app can't know whether the password was wrong or the
 * account was never created, so it asks the user, with both answers given equal weight.
 */
@Composable
fun LoginHelpCard(
    onForgotPasswordClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CarpoolListCard(modifier = modifier, contentPadding = PaddingValues(0.dp)) {
        Text(
            text = stringResource(Res.string.login_help_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = Spacing.lg, end = Spacing.lg, top = Spacing.lg, bottom = Spacing.xs)
                .semantics { heading() },
        )
        EqualHeightColumn {
            LoginHelpRow(
                title = stringResource(Res.string.login_help_forgot_title),
                subtitle = stringResource(Res.string.login_help_forgot_subtitle),
                icon = vectorResource(Res.drawable.lock_24px),
                onClick = onForgotPasswordClick,
            )
            Column {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                LoginHelpRow(
                    title = stringResource(Res.string.login_help_register_title),
                    subtitle = stringResource(Res.string.login_help_register_subtitle),
                    icon = vectorResource(Res.drawable.person_add_24px),
                    onClick = onCreateAccountClick,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun LoginHelpRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(start = Spacing.lg, end = Spacing.md, top = Spacing.md, bottom = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            imageVector = vectorResource(Res.drawable.chevron_right_24px),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Preview
@Composable
private fun LoginHelpCardPreview() {
    CarpoolTheme {
        LoginHelpCard(
            onForgotPasswordClick = {},
            onCreateAccountClick = {},
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}
