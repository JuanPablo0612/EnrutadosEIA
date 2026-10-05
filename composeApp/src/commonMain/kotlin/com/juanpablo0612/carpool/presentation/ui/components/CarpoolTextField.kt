package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_24px
import enrutadoseia.composeapp.generated.resources.hide_password
import enrutadoseia.composeapp.generated.resources.show_password
import enrutadoseia.composeapp.generated.resources.visibility_24px
import enrutadoseia.composeapp.generated.resources.visibility_off_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * The app's text field: a bold label above a white outlined field, with inline error text below.
 *
 * The label is drawn outside the field so it never collapses into the border; it comes right
 * before the field in reading order.
 *
 * An error adds a trailing error icon only when the caller has no trailing control of its own
 * (a password toggle must stay reachable while the field is in error).
 *
 * [prefix] and [suffix] render fixed text around the value, e.g. "$" or "@eia.edu.co".
 *
 * [isError] marks the field without a message of its own, for fields whose error is shown
 * elsewhere (e.g. under a group of fields too narrow to hold it).
 */
@Composable
fun CarpoolTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: String? = null,
    suffix: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    errorMessage: String? = null,
    isError: Boolean = errorMessage != null,
    supportingText: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.medium,
    colors: TextFieldColors = carpoolTextFieldColors(),
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            // Not a Spacing step (4/8dp both read as an even bigger jump from the label than the
            // original 6dp): this is a tight label-to-field coupling, not layout rhythm.
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyLarge,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = leadingIcon,
            trailingIcon = when {
                trailingIcon != null -> trailingIcon
                errorMessage != null -> {
                    { Icon(vectorResource(Res.drawable.error_24px), contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                }
                else -> null
            },
            prefix = prefix?.let { { Text(text = it, style = MaterialTheme.typography.bodyLarge) } },
            suffix = suffix?.let {
                { Text(text = it, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            },
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            isError = isError,
            enabled = enabled,
            shape = shape,
            colors = colors,
            singleLine = singleLine,
            minLines = minLines,
            supportingText = errorMessage?.let {
                {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } ?: supportingText
        )
    }
}

/**
 * White field on the neutral ground. The resting border uses `outline`, which keeps the 3:1
 * contrast a field boundary needs; `outlineVariant` is reserved for decorative hairlines.
 */
@Composable
fun carpoolTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    errorContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    errorBorderColor = MaterialTheme.colorScheme.error,
    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    errorLeadingIconColor = MaterialTheme.colorScheme.error,
)

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    supportingText: @Composable (() -> Unit)? = null,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: ImageVector? = null,
) {
    CarpoolTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        modifier = modifier,
        errorMessage = errorMessage,
        supportingText = supportingText,
        leadingIcon = leadingIcon?.let { { Icon(imageVector = it, contentDescription = null) } },
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisibility) {
                val icon = if (isPasswordVisible) {
                    vectorResource(Res.drawable.visibility_off_24px)
                } else {
                    vectorResource(Res.drawable.visibility_24px)
                }
                Icon(
                    imageVector = icon,
                    contentDescription = if (isPasswordVisible) {
                        stringResource(Res.string.hide_password)
                    } else {
                        stringResource(Res.string.show_password)
                    },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction,
            autoCorrectEnabled = false
        ),
        keyboardActions = keyboardActions
    )
}

@Preview
@Composable
private fun CarpoolTextFieldPreview() {
    CarpoolTheme {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            CarpoolTextField(
                value = "",
                onValueChange = {},
                label = "Label",
                placeholder = "Placeholder",
            )
            CarpoolTextField(
                value = "invalid@",
                onValueChange = {},
                label = "Label",
                placeholder = "Placeholder",
                errorMessage = "This field has an error",
            )
        }
    }
}

@Preview
@Composable
private fun PasswordTextFieldPreview() {
    CarpoolTheme {
        PasswordTextField(
            value = "secret",
            onValueChange = {},
            label = "Password",
            placeholder = "Enter your password",
            isPasswordVisible = false,
            onTogglePasswordVisibility = {},
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}
