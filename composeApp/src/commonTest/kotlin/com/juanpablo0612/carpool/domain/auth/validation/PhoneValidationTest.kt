package com.juanpablo0612.carpool.domain.auth.validation

import kotlin.test.Test
import kotlin.test.assertEquals

class PhoneValidationTest {

    private fun error(phone: String) = (Validator.validatePhone(phone) as? ValidationResult.Error)?.error

    @Test
    fun separatorsAreStripped() {
        assertEquals("+573001234567", Validator.normalizePhone(" +57 (300) 123-45.67 "))
    }

    @Test
    fun otherCharactersAreKeptForTheValidatorToReject() {
        assertEquals("+57abc", Validator.normalizePhone("+57 abc"))
    }

    @Test
    fun aColombianMobileIsValid() {
        assertEquals(ValidationResult.Success, Validator.validatePhone("+57 300 123 4567"))
    }

    @Test
    fun numbersFromOtherCountriesAreValid() {
        assertEquals(ValidationResult.Success, Validator.validatePhone("+1 (415) 555-2671"))
        assertEquals(ValidationResult.Success, Validator.validatePhone("+34 612 34 56 78"))
        assertEquals(ValidationResult.Success, Validator.validatePhone("+44 7911 123456"))
    }

    @Test
    fun blankIsEmpty() {
        assertEquals(ValidationError.PhoneEmpty, error("   "))
    }

    @Test
    fun theCountryCodeIsRequired() {
        assertEquals(ValidationError.PhoneInvalid, error("3001234567"))
    }

    @Test
    fun theCountryCodeCannotStartWithZero() {
        assertEquals(ValidationError.PhoneInvalid, error("+0573001234567"))
    }

    @Test
    fun eightToFifteenDigitsAreAccepted() {
        assertEquals(ValidationResult.Success, Validator.validatePhone("+12345678"))
        assertEquals(ValidationResult.Success, Validator.validatePhone("+123456789012345"))
        assertEquals(ValidationError.PhoneInvalid, error("+1234567"))
        assertEquals(ValidationError.PhoneInvalid, error("+1234567890123456"))
    }

    @Test
    fun aPlusOutsideTheStartIsInvalid() {
        assertEquals(ValidationError.PhoneInvalid, error("57+3001234567"))
        assertEquals(ValidationError.PhoneInvalid, error("++573001234567"))
    }

    @Test
    fun lettersAreInvalid() {
        assertEquals(ValidationError.PhoneInvalid, error("+57 300 ABC 4567"))
    }
}
