package com.juanpablo0612.carpool.domain.auth.validation

/** How strong a new password is, shown as a meter while the user types it. */
sealed class PasswordStrength {
    /** Shorter than the minimum; [Validator.validatePassword] rejects it. */
    data object Weak : PasswordStrength()

    /** Acceptable, but short or made of a single kind of character. */
    data object Medium : PasswordStrength()

    /** Long, and mixing in a digit or a capital letter. */
    data object Strong : PasswordStrength()

    companion object {
        /** The minimum length [Validator.validatePassword] enforces. */
        const val MIN_LENGTH = 8
        private const val STRONG_LENGTH = 12

        fun of(password: String): PasswordStrength {
            val hasVariety = password.any { it.isDigit() } || password.any { it.isUpperCase() }
            return when {
                password.length < MIN_LENGTH -> Weak
                password.length >= STRONG_LENGTH && hasVariety -> Strong
                else -> Medium
            }
        }
    }
}
