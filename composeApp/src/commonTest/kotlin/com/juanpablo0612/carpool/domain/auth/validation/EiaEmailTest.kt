package com.juanpablo0612.carpool.domain.auth.validation

import kotlin.test.Test
import kotlin.test.assertEquals

class EiaEmailTest {

    @Test
    fun aBareUsernameGetsTheEiaDomain() {
        assertEquals("juan.perez@eia.edu.co", EiaEmail.fromInput("juan.perez"))
    }

    @Test
    fun surroundingWhitespaceIsDropped() {
        assertEquals("juan.perez@eia.edu.co", EiaEmail.fromInput("  juan.perez "))
    }

    @Test
    fun aPastedFullAddressIsKept() {
        assertEquals("juan.perez@eia.edu.co", EiaEmail.fromInput("juan.perez@eia.edu.co"))
    }

    @Test
    fun anotherDomainIsKeptForTheValidatorToReject() {
        val email = EiaEmail.fromInput("juan@gmail.com")
        assertEquals("juan@gmail.com", email)
        assertEquals(ValidationResult.Error(ValidationError.EmailNotEia), Validator.validateEmail(email))
    }

    @Test
    fun emptyInputStaysEmptySoTheValidatorReportsIt() {
        assertEquals("", EiaEmail.fromInput("   "))
        assertEquals(ValidationResult.Error(ValidationError.EmailEmpty), Validator.validateEmail(""))
    }

    @Test
    fun anEiaAddressGoesBackToItsUsername() {
        assertEquals("juan.perez", EiaEmail.toInput("juan.perez@eia.edu.co"))
        assertEquals("Juan.Perez", EiaEmail.toInput(" Juan.Perez@EIA.EDU.CO "))
    }

    @Test
    fun anotherAddressIsPrefilledWhole() {
        assertEquals("juan@gmail.com", EiaEmail.toInput("juan@gmail.com"))
    }

    @Test
    fun prefillingAndSubmittingAgainKeepsTheAddress() {
        val address = "juan.perez@eia.edu.co"
        assertEquals(address, EiaEmail.fromInput(EiaEmail.toInput(address)))
    }
}
