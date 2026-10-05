package com.juanpablo0612.carpool.domain.auth.model

/**
 * A mobile number kept as the two parts the user types, digits only: the country calling code
 * without "+" (e.g. "57") and the national number (e.g. "3001234567"). Both are stored because
 * splitting an E.164 string back into them is ambiguous, and the profile editor shows them apart.
 */
data class PhoneNumber(
    val countryCode: String,
    val number: String,
) {
    /** The number in international E.164 form, e.g. "+573001234567". */
    val e164: String get() = "+$countryCode$number"

    companion object {
        /** Colombia: what almost every EIA user has, so the country code field starts with it. */
        const val DEFAULT_COUNTRY_CODE = "57"

        /** Country calling codes are one to three digits. */
        const val COUNTRY_CODE_MAX_LENGTH = 3

        /** E.164 caps a full number, country code included, at 15 digits. */
        const val MAX_DIGITS = 15

        /** Shortest full number accepted, country code included. */
        const val MIN_DIGITS = 8
    }
}
