package com.juanpablo0612.carpool.domain.place.service

import com.juanpablo0612.carpool.domain.place.model.AutocompleteSuggestion
import com.juanpablo0612.carpool.domain.place.model.Coordinates

interface PlacesSearchService {
    suspend fun search(query: String, sessionToken: String): List<AutocompleteSuggestion>
    suspend fun resolvePlace(placeId: String, sessionToken: String): Coordinates?
    suspend fun getPlaceAddress(placeId: String): String?
    suspend fun reverseGeocode(coordinates: Coordinates): String?
}
