package com.juanpablo0612.carpool.domain.place.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GeoDistanceTest {

    private fun place(name: String = "", lat: Double, lng: Double, id: String = "") =
        Place(id = id, name = name, address = "", latitude = lat, longitude = lng)

    @Test
    fun samePointIsZero() {
        assertEquals(0.0, GeoDistance.haversineMeters(6.16, -75.49, 6.16, -75.49))
    }

    @Test
    fun isSymmetric() {
        val ab = GeoDistance.haversineMeters(6.17, -75.58, 6.16, -75.49)
        val ba = GeoDistance.haversineMeters(6.16, -75.49, 6.17, -75.58)
        assertEquals(ab, ba, 1e-9)
    }

    @Test
    fun oneDegreeOfLatitude() {
        assertEquals(111_195.0, GeoDistance.haversineMeters(0.0, 0.0, 1.0, 0.0), 1.0)
    }

    @Test
    fun campusesAreAboutElevenKilometresApart() {
        val lp = Place.EIA_LAS_PALMAS
        val zu = Place.EIA_ZUNIGA
        assertEquals(11_600.0, GeoDistance.haversineMeters(lp.latitude, lp.longitude, zu.latitude, zu.longitude), 100.0)
    }

    @Test
    fun crossesTheAntimeridianTheShortWay() {
        assertEquals(22_239.0, GeoDistance.haversineMeters(0.0, 179.9, 0.0, -179.9), 5.0)
    }

    @Test
    fun nearThePoleIsFinite() {
        assertTrue(GeoDistance.haversineMeters(89.9, 0.0, 89.9, 180.0).isFinite())
    }

    @Test
    fun coordinateValidity() {
        assertFalse(place(lat = 0.0, lng = 0.0).hasValidCoordinates())
        assertFalse(place(lat = Double.NaN, lng = -75.0).hasValidCoordinates())
        assertFalse(place(lat = 91.0, lng = -75.0).hasValidCoordinates())
        assertTrue(place(lat = 6.16, lng = -75.49).hasValidCoordinates())
    }

    @Test
    fun sameCampusMatchesAtZeroEvenWithDriftedCoordinates() {
        val drifted = Place.EIA_LAS_PALMAS.copy(latitude = 6.17)
        assertEquals(0.0, matchDistanceMeters(drifted, Place.EIA_LAS_PALMAS))
    }

    @Test
    fun sameIdWithoutCampusFlagUsesHaversine() {
        val a = place(id = "x", lat = 6.16, lng = -75.49)
        val b = place(id = "x", lat = 6.17, lng = -75.49)
        assertTrue(matchDistanceMeters(a, b)!! > 1_000.0)
    }

    @Test
    fun placesWithoutCoordinatesNeverMatch() {
        val missing = place(name = "Parque Envigado", lat = 0.0, lng = 0.0)
        assertNull(matchDistanceMeters(missing, missing.copy()))
        assertNull(matchDistanceMeters(missing, place(lat = 6.17, lng = -75.58)))
    }
}
