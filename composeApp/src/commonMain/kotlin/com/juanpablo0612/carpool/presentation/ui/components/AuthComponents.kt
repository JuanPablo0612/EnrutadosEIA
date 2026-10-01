package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.auth.validation.EiaEmail
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.ContentWidth
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.ScreenInsets
import com.juanpablo0612.carpool.presentation.ui.util.TopBarInsets
import com.juanpablo0612.carpool.presentation.ui.util.centeredContent
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.arrow_back_24px
import enrutadoseia.composeapp.generated.resources.cd_back
import enrutadoseia.composeapp.generated.resources.route_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * The auth flow's top row: an optional back button and whatever the screen puts beside it (the
 * sign-up progress bar). Auth screens carry their title in the content, as a large [AuthHeader],
 * so the bar itself has none.
 */
@Composable
fun AuthTopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    content: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(TopBarInsets)
            .heightIn(min = 64.dp)
            .padding(start = Spacing.xs, end = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showBackButton) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = vectorResource(Res.drawable.arrow_back_24px),
                    contentDescription = stringResource(Res.string.cd_back)
                )
            }
        } else {
            Spacer(modifier = Modifier.width(Spacing.md))
        }
        content()
    }
}

/**
 * The frame every auth screen shares: [topBar], then [content] at the top and [footer] pinned to
 * the bottom of the screen when everything fits, or following the content when it scrolls
 * (small screens, landscape, large font scales, or the keyboard open).
 */
@Composable
fun AuthFormLayout(
    topBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    footer: @Composable ColumnScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        contentWindowInsets = ScreenInsets,
        modifier = modifier,
        topBar = topBar,
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        // heightIn(min = maxHeight) rather than a bare verticalScroll: inside a scrollable the
        // column is measured with unbounded height, so SpaceBetween would have no slack and the
        // footer would never reach the bottom.
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight)
                    .centeredContent(ContentWidth.form)
                    .padding(horizontal = Spacing.screenHorizontalForm, vertical = Spacing.lg),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(content = content)
                Column(
                    modifier = Modifier.padding(top = Spacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = footer,
                )
            }
        }
    }
}

/**
 * The large heading that opens every auth screen: an optional icon on a tile (the app mark by
 * default; `null` for none), a display-size [title] marked as the screen's heading, and an
 * optional [subtitle].
 */
@Composable
fun AuthHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = vectorResource(Res.drawable.route_24px),
    iconContainerColor: Color = MaterialTheme.colorScheme.primary,
    iconTint: Color = MaterialTheme.colorScheme.onPrimary,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(iconContainerColor, MaterialTheme.shapes.large),
                contentAlignment = Alignment.Center,
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(30.dp))
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.semantics { heading() },
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** A quiet, non-interactive hint on a white card, e.g. "check your spam folder". */
@Composable
fun AuthNote(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

/**
 * The institutional email field: the user types the username and the EIA domain is shown as a
 * fixed suffix. The typed value is completed with [EiaEmail.fromInput] when the form submits.
 */
@Composable
fun EiaEmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    supportingText: @Composable (() -> Unit)? = null,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    CarpoolTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        modifier = modifier,
        // A pasted full address already carries its domain; repeating it would read as
        // "name@eia.edu.co@eia.edu.co".
        suffix = if ('@' in value) null else "@${EiaEmail.DOMAIN}",
        errorMessage = errorMessage,
        supportingText = supportingText,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction,
            autoCorrectEnabled = false
        ),
        keyboardActions = keyboardActions
    )
}

@Preview
@Composable
private fun AuthTopBarPreview() {
    CarpoolTheme {
        AuthTopBar(onBackClick = {})
    }
}

@Preview
@Composable
private fun AuthHeaderPreview() {
    CarpoolTheme {
        AuthHeader(
            title = "Hola de nuevo",
            subtitle = "Entra con tu correo de la EIA.",
            modifier = Modifier.padding(Spacing.xl),
        )
    }
}

@Preview
@Composable
private fun EiaEmailTextFieldPreview() {
    CarpoolTheme {
        EiaEmailTextField(
            value = "juan.perez",
            onValueChange = {},
            label = "Correo universitario",
            placeholder = "nombre.apellido",
            modifier = Modifier.padding(Spacing.xl),
        )
    }
}
