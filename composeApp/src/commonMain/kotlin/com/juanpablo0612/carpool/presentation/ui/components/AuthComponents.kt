package com.juanpablo0612.carpool.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.arrow_back_24px
import enrutadoseia.composeapp.generated.resources.check_circle_24px
import enrutadoseia.composeapp.generated.resources.error_24px
import enrutadoseia.composeapp.generated.resources.hide_password
import enrutadoseia.composeapp.generated.resources.lock_24px
import enrutadoseia.composeapp.generated.resources.mail_24px
import enrutadoseia.composeapp.generated.resources.person_24px
import enrutadoseia.composeapp.generated.resources.show_password
import enrutadoseia.composeapp.generated.resources.visibility_24px
import enrutadoseia.composeapp.generated.resources.visibility_off_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = vectorResource(Res.drawable.arrow_back_24px),
                    contentDescription = null
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        )
    )
}

@Composable
fun AuthHeader(
    title: String,
    subtitle: String,
    imageRes: org.jetbrains.compose.resources.DrawableResource,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = org.jetbrains.compose.resources.painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    errorMessage: String? = null,
    supportingText: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(12.dp),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        errorBorderColor = MaterialTheme.colorScheme.error,
        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        errorLeadingIconColor = MaterialTheme.colorScheme.error,
    )
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = if (errorMessage != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { 
                Text(
                    text = placeholder, 
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                ) 
            },
            leadingIcon = leadingIcon,
            trailingIcon = if (errorMessage != null) {
                { Icon(vectorResource(Res.drawable.error_24px), contentDescription = null, tint = MaterialTheme.colorScheme.error) }
            } else trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            isError = errorMessage != null,
            enabled = enabled,
            shape = shape,
            colors = colors,
            singleLine = singleLine,
            supportingText = errorMessage?.let {
                {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } ?: supportingText
        )
    }
}

@Composable
fun EmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    supportingText: @Composable (() -> Unit)? = null,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: ImageVector = vectorResource(Res.drawable.mail_24px)
) {
    AuthTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        modifier = modifier,
        errorMessage = errorMessage,
        supportingText = supportingText,
        leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = null) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction,
            autoCorrectEnabled = false
        ),
        keyboardActions = keyboardActions
    )
}

@Composable
fun NameTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    supportingText: @Composable (() -> Unit)? = null,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: ImageVector = vectorResource(Res.drawable.person_24px)
) {
    AuthTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        modifier = modifier,
        errorMessage = errorMessage,
        supportingText = supportingText,
        leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = null) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = imeAction,
            capitalization = KeyboardCapitalization.Words,
            autoCorrectEnabled = false
        ),
        keyboardActions = keyboardActions
    )
}

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
    leadingIcon: ImageVector = vectorResource(Res.drawable.lock_24px)
) {
    AuthTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        modifier = modifier,
        errorMessage = errorMessage,
        supportingText = supportingText,
        leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = null) },
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

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    shape: Shape = RoundedCornerShape(12.dp),
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    trailingIcon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = trailingIcon, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(12.dp),
    borderColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.primary
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = shape,
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = contentColor
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.error_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun AuthDivider(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
fun AuthClickableText(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    fontWeight: FontWeight = FontWeight.Bold
) {
    Text(
        text = text,
        modifier = modifier.clickable { onClick() },
        color = color,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = fontWeight)
    )
}

@Composable
fun RoleSelectionCard(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = vectorResource(Res.drawable.check_circle_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
