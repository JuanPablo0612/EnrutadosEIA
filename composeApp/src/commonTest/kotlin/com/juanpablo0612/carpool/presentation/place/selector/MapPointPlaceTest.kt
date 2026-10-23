package com.juanpablo0612.carpool.presentation.place.selector

import kotlin.test.Test
import kotlin.test.assertEquals

class MapPointPlaceTest {

    private fun place(placeName: String?, address: String?) =
        mapPointPlace(latitude = 6.2, longitude = -75.6, placeName = placeName, address = address, fallbackName = "Pin")

    @Test
    fun aTappedPointOfInterestKeepsItsName() {
        val place = place(placeName = "Parque Envigado", address = "Cra 43 #38 Sur")
        assertEquals("Parque Envigado", place.name)
        assertEquals("Cra 43 #38 Sur", place.address)
        assertEquals(6.2, place.latitude)
        assertEquals(-75.6, place.longitude)
    }

    @Test
    fun aDroppedPinUsesTheFallbackName() {
        assertEquals("Pin", place(placeName = null, address = "Cra 43").name)
        assertEquals("Pin", place(placeName = "  ", address = "Cra 43").name)
    }

    @Test
    fun anUnresolvedAddressIsEmpty() {
        assertEquals("", place(placeName = null, address = null).address)
    }

    @Test
    fun aMapPointIsNeverASavedPlace() {
        assertEquals(MAP_POINT_PLACE_ID, place(placeName = null, address = null).id)
    }
}
