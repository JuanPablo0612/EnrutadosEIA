package com.juanpablo0612.carpool.domain.places.service

import com.juanpablo0612.carpool.domain.places.model.AutocompleteSuggestion
import com.juanpablo0612.carpool.domain.places.model.Coordinates

interface PlacesSearchService {
    suspend fun search(query: String): List<AutocompleteSuggestion>
    suspend fun getCoordinates(placeId: String): Coordinates?
    suspend fun reverseGeocode(coordinates: Coordinates): String?
}
