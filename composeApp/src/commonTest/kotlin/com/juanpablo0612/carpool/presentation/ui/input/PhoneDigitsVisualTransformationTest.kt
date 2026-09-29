package com.juanpablo0612.carpool.presentation.ui.input

import androidx.compose.ui.text.AnnotatedString
import kotlin.test.Test
import kotlin.test.assertEquals

class PhoneDigitsVisualTransformationTest {

    private fun transform(digits: String) = PhoneDigitsVisualTransformation().filter(AnnotatedString(digits))

    @Test
    fun groupsTheDigitsWithoutTheCountryCode() {
        assertEquals("300 123 4567", transform("3001234567").text.text)
        assertEquals("300 12", transform("30012").text.text)
        assertEquals("", transform("").text.text)
    }

    @Test
    fun theCursorSkipsTheSpaces() {
        val mapping = transform("3001234567").offsetMapping
        assertEquals(3, mapping.originalToTransformed(3))
        assertEquals(5, mapping.originalToTransformed(4))
        assertEquals(12, mapping.originalToTransformed(10))
        assertEquals(3, mapping.transformedToOriginal(4))
        assertEquals(10, mapping.transformedToOriginal(12))
    }
}
