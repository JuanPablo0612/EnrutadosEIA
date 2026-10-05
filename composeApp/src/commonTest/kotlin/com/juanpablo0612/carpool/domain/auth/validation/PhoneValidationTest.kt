package com.juanpablo0612.carpool.domain.auth.validation

import com.juanpablo0612.carpool.domain.auth.model.PhoneNumber
import kotlin.test.Test
import kotlin.test.assertEquals

class PhoneValidationTest {

    private fun ValidationResult.error() = (this as? ValidationResult.Error)?.error

    private fun countryCodeError(countryCode: String) = Validator.validatePhoneCountryCode(countryCode).error()

    private fun numberError(countryCode: String, number: String) =
        Validator.validatePhoneNumber(countryCode, number).error()

    @Test
    fun e164JoinsThePartsAfterAPlus() {
        assertEquals("+573001234567", PhoneNumber("57", "3001234567").e164)
    }

    @Test
    fun countryCodesOfOneToThreeDigitsAreValid() {
        assertEquals(ValidationResult.Success, Validator.validatePhoneCountryCode("1"))
        assertEquals(ValidationResult.Success, Validator.validatePhoneCountryCode("57"))
        assertEquals(ValidationResult.Success, Validator.validatePhoneCountryCode("593"))
    }

    @Test
    fun aBlankCountryCodeIsEmpty() {
        assertEquals(ValidationError.PhoneCountryCodeEmpty, countryCodeError(""))
        assertEquals(ValidationError.PhoneCountryCodeEmpty, countryCodeError("  "))
    }

    @Test
    fun aCountryCodeCannotStartWithZero() {
        assertEquals(ValidationError.PhoneCountryCodeInvalid, countryCodeError("0"))
        assertEquals(ValidationError.PhoneCountryCodeInvalid, countryCodeError("057"))
    }

    @Test
    fun aCountryCodeIsAtMostThreeDigits() {
        assertEquals(ValidationError.PhoneCountryCodeInvalid, countryCodeError("1234"))
    }

    @Test
    fun aCountryCodeIsDigitsOnly() {
        assertEquals(ValidationError.PhoneCountryCodeInvalid, countryCodeError("+57"))
        assertEquals(ValidationError.PhoneCountryCodeInvalid, countryCodeError("5a"))
    }

    @Test
    fun aColombianMobileIsValid() {
        assertEquals(ValidationResult.Success, Validator.validatePhoneNumber("57", "3001234567"))
    }

    @Test
    fun numbersFromOtherCountriesAreValid() {
        assertEquals(ValidationResult.Success, Validator.validatePhoneNumber("1", "4155552671"))
        assertEquals(ValidationResult.Success, Validator.validatePhoneNumber("34", "612345678"))
        assertEquals(ValidationResult.Success, Validator.validatePhoneNumber("44", "7911123456"))
    }

    @Test
    fun aBlankNumberIsEmpty() {
        assertEquals(ValidationError.PhoneNumberEmpty, numberError("57", ""))
    }

    @Test
    fun aNumberIsDigitsOnly() {
        assertEquals(ValidationError.PhoneNumberInvalid, numberError("57", "300 123 4567"))
        assertEquals(ValidationError.PhoneNumberInvalid, numberError("57", "300ABC4567"))
    }

    @Test
    fun theFullNumberHasEightToFifteenDigits() {
        assertEquals(ValidationResult.Success, Validator.validatePhoneNumber("1", "2345678"))
        assertEquals(ValidationResult.Success, Validator.validatePhoneNumber("123", "456789012345"))
        assertEquals(ValidationError.PhoneNumberInvalid, numberError("1", "234567"))
        assertEquals(ValidationError.PhoneNumberInvalid, numberError("123", "4567890123456"))
    }
}
