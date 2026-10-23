package com.juanpablo0612.carpool.presentation.profile

import com.juanpablo0612.carpool.domain.payment.model.PaymentMethod
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethodType
import com.juanpablo0612.carpool.domain.vehicle.model.Vehicle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProfileSummariesTest {

    private fun vehicle(id: String, isPrimary: Boolean = false) = Vehicle(
        id = id,
        driverId = "d1",
        brand = "Mazda",
        model = "3",
        licensePlate = "ABC123",
        color = "Gris",
        year = 2020,
        seatsAvailable = 4,
        isPrimary = isPrimary,
    )

    @Test
    fun noVehiclesFeaturesNone() {
        val summary = emptyList<Vehicle>().toVehicleSummary()
        assertNull(summary.featured)
        assertEquals(0, summary.count)
    }

    @Test
    fun thePrimaryVehicleIsFeaturedOverTheFirst() {
        val summary = listOf(vehicle("a"), vehicle("b", isPrimary = true), vehicle("c")).toVehicleSummary()
        assertEquals("b", summary.featured?.id)
        assertEquals(3, summary.count)
    }

    @Test
    fun withoutAPrimaryTheFirstVehicleIsFeatured() {
        assertEquals("a", listOf(vehicle("a"), vehicle("b")).toVehicleSummary().featured?.id)
    }

    @Test
    fun theSummaryLineReadsLikeTheCar() {
        assertEquals("Mazda 3 gris · ABC 123", vehicle("a").summaryLine())
    }

    @Test
    fun blankPartsAreLeftOutOfTheSummaryLine() {
        assertEquals("Mazda 3", vehicle("a").copy(color = "", licensePlate = "").summaryLine())
    }

    @Test
    fun onlySixCharacterPlatesAreSpaced() {
        assertEquals("ABC 12D", displayPlate("ABC12D"))
        assertEquals("AB12", displayPlate("AB12"))
    }

    @Test
    fun paymentKindsAreDistinctAndInPickerOrder() {
        val summary = listOf(
            PaymentMethod.QrCode(id = "1", imageUrl = ""),
            PaymentMethod.BreBKey(id = "2", key = "@a"),
            PaymentMethod.BreBKey(id = "3", key = "@b"),
        ).toPaymentSummary()
        assertEquals(3, summary.count)
        assertEquals(listOf(PaymentMethodType.BreBKey, PaymentMethodType.QrCode), summary.kinds)
    }

    @Test
    fun noPaymentMethodsHaveNoKinds() {
        val summary = emptyList<PaymentMethod>().toPaymentSummary()
        assertEquals(0, summary.count)
        assertEquals(emptyList(), summary.kinds)
    }
}
