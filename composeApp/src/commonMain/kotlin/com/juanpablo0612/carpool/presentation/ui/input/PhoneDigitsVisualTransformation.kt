package com.juanpablo0612.carpool.presentation.ui.input

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Shows a Colombian mobile number's 10 digits grouped as "300 123 4567". The country code isn't
 * part of it: the field shows "+57" as its fixed prefix, so the value stays the bare digits.
 */
class PhoneDigitsVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(MAX_DIGITS)
        val formatted = buildString {
            digits.forEachIndexed { i, c ->
                if (i in GROUP_BREAKS) append(' ')
                append(c)
            }
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clamped = offset.coerceIn(0, digits.length)
                return clamped + GROUP_BREAKS.count { clamped > it }
            }

            override fun transformedToOriginal(offset: Int): Int {
                val clamped = offset.coerceIn(0, formatted.length)
                return clamped - formatted.take(clamped).count { it == ' ' }
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }

    private companion object {
        const val MAX_DIGITS = 10
        val GROUP_BREAKS = listOf(3, 6)
    }
}
