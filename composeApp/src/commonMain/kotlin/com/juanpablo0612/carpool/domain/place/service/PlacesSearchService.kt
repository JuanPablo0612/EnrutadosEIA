package com.juanpablo0612.carpool.domain.place.service

import com.juanpablo0612.carpool.domain.place.model.AutocompleteSuggestion
import com.juanpablo0612.carpool.domain.place.model.Coordinates

interface PlacesSearchService {
    suspend fun search(query: String): List<AutocompleteSuggestion>
    suspend fun reverseGeocode(coordinates: Coordinates): String?
}
