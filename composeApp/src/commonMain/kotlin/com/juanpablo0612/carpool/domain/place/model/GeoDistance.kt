package com.juanpablo0612.carpool.domain.place.model

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object GeoDistance {
    /** Mean Earth radius (IUGG). */
    const val EARTH_RADIUS_METERS = 6_371_008.8

    /** Great-circle distance in meters between two coordinates, using the haversine formula. */
    fun haversineMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val phi1 = lat1.toRadians()
        val phi2 = lat2.toRadians()
        val deltaPhi = (lat2 - lat1).toRadians()
        val deltaLambda = (lon2 - lon1).toRadians()
        val a = sin(deltaPhi / 2).squared() + cos(phi1) * cos(phi2) * sin(deltaLambda / 2).squared()
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_METERS * c
    }

    private fun Double.toRadians() = this * PI / 180.0
    private fun Double.squared() = this * this
}

/**
 * Whether this place carries usable coordinates. (0.0, 0.0) is the DTO default for a missing
 * location, so it is treated as absent rather than as a point in the Gulf of Guinea.
 */
fun Place.hasValidCoordinates(): Boolean =
    latitude.isFinite() && longitude.isFinite() &&
        latitude in -90.0..90.0 && longitude in -180.0..180.0 &&
        !(latitude == 0.0 && longitude == 0.0)

/**
 * How far apart two places are for trip-matching purposes, or `null` when they can't be compared.
 *
 * The same campus preset always matches at distance 0, even if a stored copy has drifted
 * coordinates. A place without usable coordinates never matches.
 */
fun matchDistanceMeters(a: Place, b: Place): Double? = when {
    a.isCampusPreset && b.isCampusPreset && a.id.isNotBlank() && a.id == b.id -> 0.0
    a.hasValidCoordinates() && b.hasValidCoordinates() ->
        GeoDistance.haversineMeters(a.latitude, a.longitude, b.latitude, b.longitude)
    else -> null
}
