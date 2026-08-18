package com.juanpablo0612.carpool.data.place.datasource

import com.juanpablo0612.carpool.domain.place.model.AutocompleteSuggestion
import com.juanpablo0612.carpool.domain.place.model.Coordinates
import com.juanpablo0612.carpool.domain.place.service.PlacesSearchService

class CompassPlacesSearchService : PlacesSearchService {
    private val autocomplete by lazy { createAutocomplete() }
    private val geocoder by lazy { createGeocoder() }

    override suspend fun search(query: String): List<AutocompleteSuggestion> {
        if (query.isBlank()) return emptyList()
        return try {
            autocomplete.search(query).getOrNull()?.map { place ->
                val primary = place.name ?: place.thoroughfare ?: ""
                val secondary = place.locality ?: place.administrativeArea ?: ""
                val full = place.name
                    ?: listOfNotNull(place.thoroughfare, place.locality, place.administrativeArea)
                        .joinToString(", ")
                AutocompleteSuggestion(
                    latitude = place.coordinates.latitude,
                    longitude = place.coordinates.longitude,
                    primaryText = primary,
                    secondaryText = secondary,
                    fullAddress = full,
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun reverseGeocode(coordinates: Coordinates): String? {
        return try {
            geocoder.reverse(coordinates.latitude, coordinates.longitude)
                .getOrNull()
                ?.firstOrNull()
                ?.let { place ->
                    place.name
                        ?: listOfNotNull(place.thoroughfare, place.locality, place.administrativeArea)
                            .joinToString(", ")
                            .takeIf { it.isNotBlank() }
                }
        } catch (e: Exception) {
            null
        }
    }
}
