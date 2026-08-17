package com.juanpablo0612.carpool.presentation.ui.input

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class ColombianPhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(10)
        val formatted = buildString {
            append("+57 ")
            digits.forEachIndexed { i, c ->
                if (i == 3 || i == 6) append(" ")
                append(c)
            }
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clamped = offset.coerceAtMost(digits.length)
                var extra = 4 // "+57 "
                if (clamped > 3) extra++
                if (clamped > 6) extra++
                return clamped + extra
            }
            override fun transformedToOriginal(offset: Int): Int {
                val raw = (offset - 4).coerceAtLeast(0)
                return raw.coerceAtMost(digits.length)
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
