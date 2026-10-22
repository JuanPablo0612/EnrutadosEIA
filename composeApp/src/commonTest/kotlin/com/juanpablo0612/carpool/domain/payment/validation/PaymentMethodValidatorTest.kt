package com.juanpablo0612.carpool.domain.payment.validation

import com.juanpablo0612.carpool.domain.payment.model.PaymentMethod
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethodDraft
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PaymentMethodValidatorTest {

    private fun validateKey(key: String) =
        PaymentMethodValidator.validate(PaymentMethodValidator.normalize(PaymentMethodDraft.BreBKey(key)))

    @Test
    fun everyKindOfBreBKeyIsAccepted() {
        listOf("3001234567", "1037654321", "carolina@eia.edu.co", "@carolina.r").forEach { key ->
            assertEquals(emptyList(), validateKey(key), key)
        }
    }

    @Test
    fun surroundingWhitespaceIsTrimmed() {
        val draft = PaymentMethodValidator.normalize(PaymentMethodDraft.BreBKey("  @carolina \n"))
        assertEquals(PaymentMethodDraft.BreBKey("@carolina"), draft)
    }

    @Test
    fun aBlankKeyIsRequired() {
        assertEquals(listOf(PaymentMethodValidationError.KeyRequired), validateKey("   "))
    }

    @Test
    fun aKeyWithInnerSpacesIsRejected() {
        assertEquals(listOf(PaymentMethodValidationError.KeyHasSpaces), validateKey("300 123 4567"))
    }

    @Test
    fun aKeyOverTheCapIsRejected() {
        val max = "a".repeat(PaymentMethodValidator.BRE_B_KEY_MAX_LENGTH)
        assertEquals(emptyList(), validateKey(max))
        assertEquals(listOf(PaymentMethodValidationError.KeyTooLong), validateKey(max + "a"))
    }

    @Test
    fun aQrCodeNeedsAnImage() {
        assertEquals(
            listOf(PaymentMethodValidationError.ImageRequired),
            PaymentMethodValidator.validate(PaymentMethodDraft.QrCode(ByteArray(0))),
        )
        assertEquals(emptyList(), PaymentMethodValidator.validate(PaymentMethodDraft.QrCode(byteArrayOf(1))))
    }

    @Test
    fun aKeyAlreadySavedIsADuplicateIgnoringCase() {
        val existing = listOf(PaymentMethod.BreBKey("1", "@Carolina"), PaymentMethod.QrCode("2", "https://qr"))
        assertTrue(PaymentMethodValidator.isDuplicate(PaymentMethodDraft.BreBKey("@carolina"), existing))
        assertFalse(PaymentMethodValidator.isDuplicate(PaymentMethodDraft.BreBKey("@other"), existing))
        assertFalse(PaymentMethodValidator.isDuplicate(PaymentMethodDraft.QrCode(byteArrayOf(1)), existing))
    }
}
