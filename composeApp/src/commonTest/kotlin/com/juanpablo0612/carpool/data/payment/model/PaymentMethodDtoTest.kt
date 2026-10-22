package com.juanpablo0612.carpool.data.payment.model

import com.juanpablo0612.carpool.domain.payment.model.PaymentMethod
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethodType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PaymentMethodDtoTest {

    @Test
    fun eachKnownTypeMapsToItsMethod() {
        assertEquals(
            PaymentMethod.BreBKey(id = "1", key = "@carolina"),
            PaymentMethodDto(id = "1", type = PaymentMethodDto.TYPE_BRE_B_KEY, key = "@carolina").toDomain(),
        )
        assertEquals(
            PaymentMethod.QrCode(id = "2", imageUrl = "https://qr"),
            PaymentMethodDto(id = "2", type = PaymentMethodDto.TYPE_QR_CODE, imageUrl = "https://qr").toDomain(),
        )
    }

    @Test
    fun anUnknownTypeIsSkipped() {
        assertNull(PaymentMethodDto(id = "1", type = "NEQUI", key = "3001234567").toDomain())
    }

    @Test
    fun aDocumentMissingItsDataIsSkipped() {
        assertNull(PaymentMethodDto(id = "1", type = PaymentMethodDto.TYPE_BRE_B_KEY).toDomain())
        assertNull(PaymentMethodDto(id = "2", type = PaymentMethodDto.TYPE_QR_CODE, imageUrl = "").toDomain())
    }

    @Test
    fun everyTypeRoundTripsThroughItsStoredName() {
        PaymentMethodType.all.forEach { type ->
            val stored = PaymentMethodDto.typeOf(type)
            val method = PaymentMethodDto(id = "x", type = stored, key = "k", imageUrl = "u").toDomain()
            assertEquals(type, method?.type)
        }
    }
}
