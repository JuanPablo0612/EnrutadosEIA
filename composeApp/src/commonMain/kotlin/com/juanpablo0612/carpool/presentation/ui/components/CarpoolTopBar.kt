package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.arrow_back_24px
import enrutadoseia.composeapp.generated.resources.cd_back
import enrutadoseia.composeapp.generated.resources.cd_open_profile
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarpoolTopBar(
    title: String,
    user: User,
    isDualRole: Boolean,
    currentRoleLabel: String = "",
    onAvatarClick: () -> Unit,
    onRoleToggle: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            val avatarLabel = stringResource(Res.string.cd_open_profile)
            // 40.dp is the visual avatar size, but the touch target is padded out to 48.dp
            // (the Material minimum) and centered on it, so the avatar's on-screen position
            // is unchanged: the extra 8.dp is split 4.dp/4.dp around the 40.dp avatar.
            Box(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = onAvatarClick)
                    .semantics { contentDescription = avatarLabel },
                contentAlignment = Alignment.Center,
            ) {
                UserAvatar(
                    name = user.name ?: user.email,
                    photoUrl = user.photoUrl,
                    size = 40.dp,
                )
            }
        },
        actions = {
            if (isDualRole && onRoleToggle != null) {
                FilterChip(
                    selected = false,
                    onClick = onRoleToggle,
                    label = { Text(currentRoleLabel) },
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
            actions()
        }
    )
}

/**
 * Shared top bar for screens that navigate back via a leading arrow. Standardizes on the
 * bold titleLarge + [MaterialTheme.colorScheme.background] container treatment already used
 * by the majority of the hand-rolled top bars this replaces (the two are visually identical
 * in this theme's color scheme, since `background` and `surface` share the same value).
 *
 * @param subtitle optional secondary line rendered under [title] in a smaller, muted style,
 * for screens that previously showed a two-line title (e.g. a list count or description).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarpoolBackTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    vectorResource(Res.drawable.arrow_back_24px),
                    contentDescription = stringResource(Res.string.cd_back)
                )
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Preview
@Composable
private fun CarpoolBackTopBarPreview() {
    CarpoolTheme {
        CarpoolBackTopBar(
            title = "Title",
            onBack = {},
        )
    }
}

@Preview
@Composable
private fun CarpoolBackTopBarWithSubtitlePreview() {
    CarpoolTheme {
        CarpoolBackTopBar(
            title = "Title",
            subtitle = "Subtitle",
            onBack = {},
        )
    }
}
