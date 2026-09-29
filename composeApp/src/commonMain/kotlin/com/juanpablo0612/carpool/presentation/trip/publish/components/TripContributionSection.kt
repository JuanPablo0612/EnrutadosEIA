package com.juanpablo0612.carpool.presentation.trip.publish.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.juanpablo0612.carpool.presentation.ui.components.CarpoolTextField
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import com.juanpablo0612.carpool.presentation.ui.util.formatPesos
import com.juanpablo0612.carpool.presentation.ui.util.groupThousands
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.trip_contribution_custom_label
import enrutadoseia.composeapp.generated.resources.trip_contribution_free
import enrutadoseia.composeapp.generated.resources.trip_contribution_hint
import enrutadoseia.composeapp.generated.resources.trip_contribution_other
import enrutadoseia.composeapp.generated.resources.trip_contribution_section
import org.jetbrains.compose.resources.stringResource

/**
 * The amounts drivers pick most often, one tap each. Carpooling here may only share costs, so the
 * presets stay small; anything else goes through "Otro". The domain validator enforces the cap.
 */
private val CONTRIBUTION_PRESETS = listOf(3_000, 4_000, 5_000)

/**
 * Contribution per passenger: "Gratis", a few presets, or "Otro" for a typed amount. A value that
 * isn't a preset (e.g. carried over from the driver's last trip) opens straight on "Otro".
 */
@Composable
internal fun TripContributionSection(
    contributionPerPassenger: Int?,
    onContributionChange: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val amount = contributionPerPassenger?.takeIf { it > 0 }
    var isCustom by rememberSaveable { mutableStateOf(amount != null && amount !in CONTRIBUTION_PRESETS) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        SectionLabel(text = stringResource(Res.string.trip_contribution_section))
        Text(
            text = stringResource(Res.string.trip_contribution_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            FilterChip(
                selected = !isCustom && amount == null,
                onClick = {
                    isCustom = false
                    onContributionChange(null)
                },
                label = { Text(stringResource(Res.string.trip_contribution_free)) },
            )
            CONTRIBUTION_PRESETS.forEach { preset ->
                FilterChip(
                    selected = !isCustom && amount == preset,
                    onClick = {
                        isCustom = false
                        onContributionChange(preset)
                    },
                    label = { Text(formatPesos(preset)) },
                )
            }
            FilterChip(
                selected = isCustom,
                onClick = { isCustom = true },
                label = { Text(stringResource(Res.string.trip_contribution_other)) },
            )
        }
        if (isCustom) {
            CarpoolTextField(
                value = amount?.toString().orEmpty(),
                onValueChange = { raw -> onContributionChange(raw.filter(Char::isDigit).take(6).toIntOrNull()) },
                label = stringResource(Res.string.trip_contribution_custom_label),
                placeholder = "",
                prefix = "$",
                visualTransformation = remember { PesosVisualTransformation() },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            )
        }
    }
}

/**
 * Shows the typed digits grouped ("12.500") while the value stays plain digits. The cursor is
 * kept at the end, since the field only ever grows or shrinks from there.
 */
private class PesosVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        val formatted = if (original.isEmpty()) "" else groupThousands(original.toIntOrNull() ?: 0)
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = formatted.length
            override fun transformedToOriginal(offset: Int): Int = original.length
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
