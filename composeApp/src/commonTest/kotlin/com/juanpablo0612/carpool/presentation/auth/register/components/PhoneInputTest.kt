package com.juanpablo0612.carpool.presentation.auth.register.components

import kotlin.test.Test
import kotlin.test.assertEquals

class PhoneInputTest {

    @Test
    fun aLeadingPlusAndDigitsAreKept() {
        assertEquals("+573001234567", phoneInput("+573001234567"))
    }

    @Test
    fun separatorsInAPastedNumberAreDropped() {
        assertEquals("+14155552671", phoneInput(" +1 (415) 555-2671"))
    }

    @Test
    fun aPlusAfterTheFirstCharacterIsDropped() {
        assertEquals("+5730", phoneInput("+57+30"))
        assertEquals("5730", phoneInput("57+30"))
    }

    @Test
    fun lettersAreDropped() {
        assertEquals("+57300", phoneInput("+57abc300"))
    }

    @Test
    fun theValueIsCappedAtTheLongestE164Number() {
        assertEquals("+123456789012345", phoneInput("+1234567890123456789"))
    }
}
