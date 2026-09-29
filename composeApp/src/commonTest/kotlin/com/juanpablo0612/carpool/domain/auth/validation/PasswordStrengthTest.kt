package com.juanpablo0612.carpool.domain.auth.validation

import kotlin.test.Test
import kotlin.test.assertEquals

class PasswordStrengthTest {

    @Test
    fun shorterThanTheMinimumIsWeak() {
        assertEquals(PasswordStrength.Weak, PasswordStrength.of(""))
        assertEquals(PasswordStrength.Weak, PasswordStrength.of("Abc1234"))
    }

    @Test
    fun theMinimumLengthIsAtLeastMedium() {
        assertEquals(PasswordStrength.Medium, PasswordStrength.of("abcdefgh"))
    }

    @Test
    fun longButSingleKindIsMedium() {
        assertEquals(PasswordStrength.Medium, PasswordStrength.of("abcdefghijklmnop"))
    }

    @Test
    fun varietyWithoutLengthIsMedium() {
        assertEquals(PasswordStrength.Medium, PasswordStrength.of("Abcdefg1"))
    }

    @Test
    fun longWithADigitOrCapitalIsStrong() {
        assertEquals(PasswordStrength.Strong, PasswordStrength.of("abcdefghijk1"))
        assertEquals(PasswordStrength.Strong, PasswordStrength.of("Abcdefghijkl"))
    }

    @Test
    fun weakMatchesWhatTheValidatorRejects() {
        val tooShort = "a".repeat(PasswordStrength.MIN_LENGTH - 1)
        assertEquals(PasswordStrength.Weak, PasswordStrength.of(tooShort))
        assertEquals(
            ValidationResult.Error(ValidationError.PasswordTooShort),
            Validator.validatePassword(tooShort),
        )
    }
}
