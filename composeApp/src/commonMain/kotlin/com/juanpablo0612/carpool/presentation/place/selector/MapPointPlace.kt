package com.juanpablo0612.carpool.presentation.place.selector

import com.juanpablo0612.carpool.domain.place.model.Place

/** The id of a place picked on the map: like the current location, it is never stored as a saved place. */
const val MAP_POINT_PLACE_ID = "map_point"

/**
 * The one-off place for a point picked on the map. A tapped point of interest keeps its name;
 * a dropped pin is called [fallbackName], with the resolved address underneath.
 */
fun mapPointPlace(
    latitude: Double,
    longitude: Double,
    placeName: String?,
    address: String?,
    fallbackName: String,
): Place = Place(
    id = MAP_POINT_PLACE_ID,
    name = placeName?.trim()?.takeIf { it.isNotEmpty() } ?: fallbackName,
    address = address?.trim().orEmpty(),
    latitude = latitude,
    longitude = longitude,
)
