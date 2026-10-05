package com.juanpablo0612.carpool.presentation.auth.register.components

import kotlin.test.Test
import kotlin.test.assertEquals

class DigitsInputTest {

    @Test
    fun digitsAreKept() {
        assertEquals("3001234567", digitsInput("3001234567", maxLength = 14))
    }

    @Test
    fun separatorsInAPastedNumberAreDropped() {
        assertEquals("3001234567", digitsInput(" (300) 123-45.67 ", maxLength = 14))
    }

    @Test
    fun aPlusIsDropped() {
        assertEquals("57", digitsInput("+57", maxLength = 3))
    }

    @Test
    fun lettersAreDropped() {
        assertEquals("300", digitsInput("3a0b0", maxLength = 14))
    }

    @Test
    fun theValueIsCappedAtTheMaximumLength() {
        assertEquals("123", digitsInput("12345", maxLength = 3))
    }
}
