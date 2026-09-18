package com.juanpablo0612.carpool.presentation.trip.create.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.trip_contribution_hint
import enrutadoseia.composeapp.generated.resources.trip_contribution_section
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TripContributionSection(
    contributionPerPassenger: Int?,
    onContributionChange: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SectionLabel(
            text = stringResource(Res.string.trip_contribution_section),
            modifier = Modifier.padding(start = Spacing.lg, end = Spacing.lg, top = Spacing.lg, bottom = Spacing.sm)
        )
        OutlinedTextField(
            value = contributionPerPassenger?.toString() ?: "",
            onValueChange = { raw ->
                val digits = raw.filter { it.isDigit() }
                onContributionChange(digits.toIntOrNull())
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg),
            prefix = { Text("$") },
            visualTransformation = remember { PesosVisualTransformation() },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = stringResource(Res.string.trip_contribution_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = Spacing.lg)
        )
    }
}

private class PesosVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        val formatted = formatPesos(original.toIntOrNull() ?: 0)
            .takeIf { original.isNotEmpty() } ?: ""

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = formatted.length
            override fun transformedToOriginal(offset: Int): Int = original.length
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

internal fun formatPesos(amount: Int): String {
    return amount.toString()
        .reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
}
