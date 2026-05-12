package com.juanpablo0612.carpool.presentation.vehicles.register

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class ColombianPlateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val display = if (raw.length > 3) "${raw.take(3)} ${raw.drop(3)}" else raw
        val offsetMap = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                if (offset <= 3) offset else offset + 1

            override fun transformedToOriginal(offset: Int): Int =
                if (offset <= 3) offset else (offset - 1).coerceAtLeast(0)
        }
        return TransformedText(AnnotatedString(display), offsetMap)
    }
}
