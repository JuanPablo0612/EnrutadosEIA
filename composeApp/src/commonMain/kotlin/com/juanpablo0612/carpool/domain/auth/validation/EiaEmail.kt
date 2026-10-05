package com.juanpablo0612.carpool.domain.auth.validation

/**
 * The institutional address every account uses. Sign-in and sign-up forms let the user type only
 * the part before the `@`, with the domain shown as a fixed suffix.
 */
object EiaEmail {
    const val DOMAIN = "eia.edu.co"

    /**
     * Builds the full address from what the user typed. A bare username gets the EIA domain;
     * anything that already contains an `@` (a pasted full address, or another domain) is kept as
     * typed so [Validator.validateEmail] can judge it.
     */
    fun fromInput(input: String): String {
        val trimmed = input.trim()
        return if (trimmed.isEmpty() || '@' in trimmed) trimmed else "$trimmed@$DOMAIN"
    }

    /**
     * The reverse of [fromInput], for prefilling a form with an address the user already typed
     * elsewhere: an EIA address goes back to its bare username (the field shows the domain as a
     * suffix); any other address is kept whole.
     */
    fun toInput(address: String): String {
        val trimmed = address.trim()
        val suffix = "@$DOMAIN"
        return if (trimmed.endsWith(suffix, ignoreCase = true)) trimmed.dropLast(suffix.length) else trimmed
    }
}
