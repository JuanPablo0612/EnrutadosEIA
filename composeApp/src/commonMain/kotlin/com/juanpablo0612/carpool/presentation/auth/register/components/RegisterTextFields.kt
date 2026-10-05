package com.juanpablo0612.carpool.presentation.auth.register.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.juanpablo0612.carpool.domain.auth.model.PhoneNumber
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTextField
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.register_phone_country_code_label
import enrutadoseia.composeapp.generated.resources.register_phone_country_code_placeholder
import enrutadoseia.composeapp.generated.resources.register_phone_format_hint
import enrutadoseia.composeapp.generated.resources.register_phone_hint
import enrutadoseia.composeapp.generated.resources.register_phone_number_label
import enrutadoseia.composeapp.generated.resources.register_phone_placeholder
import org.jetbrains.compose.resources.stringResource

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
) {
    CarpoolTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        modifier = modifier,
        errorMessage = errorMessage,
        supportingText = supportingText,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = imeAction,
            capitalization = KeyboardCapitalization.Words,
            autoCorrectEnabled = false
        ),
        keyboardActions = keyboardActions
    )
}

/**
 * A phone number as two fields: the country code, typed after a fixed "+", and the national
 * number. Both keep digits only, so nobody has to wonder whether to type the "+".
 *
 * Errors show under the pair rather than inside each field: the country code field is too narrow
 * for a sentence. The fields sit side by side while they fit and stack at large font scales.
 */
@Composable
fun PhoneNumberFields(
    countryCode: String,
    number: String,
    onCountryCodeChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    countryCodeError: String? = null,
    numberError: String? = null,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val density = LocalDensity.current
    // In sp so both widths grow with the font scale, like the text inside them.
    val countryCodeWidth = with(density) { COUNTRY_CODE_FIELD_WIDTH.toDp() }
    val minNumberWidth = with(density) { MIN_NUMBER_FIELD_WIDTH.toDp() }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = if (countryCodeError != null || numberError != null) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.padding(bottom = Spacing.sm)
        )

        val countryCodeField = @Composable { fieldModifier: Modifier ->
            CarpoolTextField(
                value = countryCode,
                onValueChange = { onCountryCodeChange(digitsInput(it, PhoneNumber.COUNTRY_CODE_MAX_LENGTH)) },
                label = stringResource(Res.string.register_phone_country_code_label),
                placeholder = stringResource(Res.string.register_phone_country_code_placeholder),
                prefix = "+",
                isError = countryCodeError != null,
                modifier = fieldModifier,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            )
        }
        val numberField = @Composable { fieldModifier: Modifier ->
            CarpoolTextField(
                value = number,
                onValueChange = { onNumberChange(digitsInput(it, NUMBER_MAX_LENGTH)) },
                label = stringResource(Res.string.register_phone_number_label),
                placeholder = stringResource(Res.string.register_phone_placeholder),
                isError = numberError != null,
                modifier = fieldModifier,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = imeAction),
                keyboardActions = keyboardActions,
            )
        }

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            if (maxWidth >= countryCodeWidth + Spacing.sm + minNumberWidth) {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    countryCodeField(Modifier.width(countryCodeWidth))
                    numberField(Modifier.weight(1f))
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    countryCodeField(Modifier.width(countryCodeWidth))
                    numberField(Modifier.fillMaxWidth())
                }
            }
        }

        // Same inset and style as an outlined field's supporting text.
        val messageModifier = Modifier.padding(start = Spacing.lg, end = Spacing.lg, top = Spacing.xs)
        listOfNotNull(countryCodeError, numberError).forEach { error ->
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = messageModifier
            )
        }
        listOf(Res.string.register_phone_format_hint, Res.string.register_phone_hint).forEach { hint ->
            Text(
                text = stringResource(hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = messageModifier
            )
        }
    }
}

private val COUNTRY_CODE_FIELD_WIDTH = 104.sp
private val MIN_NUMBER_FIELD_WIDTH = 150.sp

/** The national number gets whatever E.164's 15 digits leave after the shortest country code. */
private const val NUMBER_MAX_LENGTH = PhoneNumber.MAX_DIGITS - 1

/** Keeps the digits of what was typed or pasted, so separators in a pasted number are dropped. */
internal fun digitsInput(raw: String, maxLength: Int): String = raw.filter { it in '0'..'9' }.take(maxLength)
