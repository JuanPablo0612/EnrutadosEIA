package com.juanpablo0612.carpool.presentation.auth.register.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTextField
import com.juanpablo0612.carpool.presentation.ui.input.ColombianPhoneVisualTransformation
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.call_24px
import enrutadoseia.composeapp.generated.resources.person_24px
import org.jetbrains.compose.resources.vectorResource

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
    CarpoolTextField(
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
fun PhoneTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    CarpoolTextField(
        value = value,
        onValueChange = { onValueChange(it.filter { c -> c.isDigit() }.take(10)) },
        label = label,
        placeholder = placeholder,
        modifier = modifier,
        errorMessage = errorMessage,
        leadingIcon = { Icon(imageVector = vectorResource(Res.drawable.call_24px), contentDescription = null) },
        visualTransformation = ColombianPhoneVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions
    )
}
