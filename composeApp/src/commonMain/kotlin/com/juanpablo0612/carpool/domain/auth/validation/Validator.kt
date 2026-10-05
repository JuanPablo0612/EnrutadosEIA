package com.juanpablo0612.carpool.domain.auth.validation

import com.juanpablo0612.carpool.domain.auth.model.PhoneNumber

object Validator {
    // Basic shape check (local@domain.tld) — deliberately not exhaustive RFC 5322, just enough to
    // reject obviously malformed input before the EIA-domain-specific check runs.
    private val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Error(ValidationError.EmailEmpty)
            !EMAIL_REGEX.matches(email) -> ValidationResult.Error(ValidationError.EmailInvalid)
            !email.endsWith("@${EiaEmail.DOMAIN}", ignoreCase = true) -> ValidationResult.Error(ValidationError.EmailNotEia)
            else -> ValidationResult.Success
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Error(ValidationError.PasswordEmpty)
            password.length < PasswordStrength.MIN_LENGTH -> ValidationResult.Error(ValidationError.PasswordTooShort)
            else -> ValidationResult.Success
        }
    }

    fun validateFullName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Error(ValidationError.NameEmpty)
            name.trim().split(" ").size < 2 -> ValidationResult.Error(ValidationError.NameTooShort)
            else -> ValidationResult.Success
        }
    }

    fun validateConfirmPassword(password: String, confirm: String): ValidationResult {
        return when {
            confirm.isBlank() -> ValidationResult.Error(ValidationError.ConfirmPasswordEmpty)
            password != confirm -> ValidationResult.Error(ValidationError.PasswordsDoNotMatch)
            else -> ValidationResult.Success
        }
    }

    /** The country calling code, typed without "+": one to three digits, never starting with 0. */
    fun validatePhoneCountryCode(countryCode: String): ValidationResult {
        return when {
            countryCode.isBlank() -> ValidationResult.Error(ValidationError.PhoneCountryCodeEmpty)
            !COUNTRY_CODE_REGEX.matches(countryCode) -> ValidationResult.Error(ValidationError.PhoneCountryCodeInvalid)
            else -> ValidationResult.Success
        }
    }

    /**
     * The national number: digits only, and long enough that the full number (country code
     * included) has the 8 to 15 digits E.164 allows.
     */
    fun validatePhoneNumber(countryCode: String, number: String): ValidationResult {
        val totalDigits = countryCode.length + number.length
        return when {
            number.isBlank() -> ValidationResult.Error(ValidationError.PhoneNumberEmpty)
            !number.all { it in '0'..'9' } -> ValidationResult.Error(ValidationError.PhoneNumberInvalid)
            totalDigits !in PhoneNumber.MIN_DIGITS..PhoneNumber.MAX_DIGITS ->
                ValidationResult.Error(ValidationError.PhoneNumberInvalid)
            else -> ValidationResult.Success
        }
    }

    /**
     * Strips the separators people type or paste in a phone number (spaces, dashes, parentheses,
     * dots), so "+57 (300) 123-4567" becomes "+573001234567". Anything else is kept for
     * [validatePhone] to reject.
     */
    fun normalizePhone(phone: String): String = phone.filterNot { it.isWhitespace() || it in PHONE_SEPARATORS }

    /** Accepts an international number in E.164 form once normalized: "+", country code, 8–15 digits. */
    fun validatePhone(phone: String): ValidationResult {
        return when {
            phone.isBlank() -> ValidationResult.Error(ValidationError.PhoneEmpty)
            !E164_REGEX.matches(normalizePhone(phone)) -> ValidationResult.Error(ValidationError.PhoneInvalid)
            else -> ValidationResult.Success
        }
    }

    private const val PHONE_SEPARATORS = "-()."
    private val E164_REGEX = Regex("^\\+[1-9][0-9]{7,14}$")
    private val COUNTRY_CODE_REGEX = Regex("^[1-9][0-9]{0,2}$")
}
