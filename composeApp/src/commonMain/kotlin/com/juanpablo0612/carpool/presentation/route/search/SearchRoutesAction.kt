package com.juanpablo0612.carpool.presentation.route.search

import com.juanpablo0612.carpool.domain.place.model.Place
import com.juanpablo0612.carpool.domain.trip.model.CampusDirection

sealed class SearchRoutesAction {
    data class OnDirectionChanged(val direction: CampusDirection) : SearchRoutesAction()
    data class OnCampusSelected(val campus: Place) : SearchRoutesAction()
    data object OnPickPlace : SearchRoutesAction()
    data class OnPlaceSelected(val place: Place) : SearchRoutesAction()
    data object OnCancelPlaceSelection : SearchRoutesAction()
    data object OnClearPlace : SearchRoutesAction()
    data object OnShowDateTimeSheet : SearchRoutesAction()
    data object OnDismissDateTimeSheet : SearchRoutesAction()
    data class OnDateTimeChanged(val epochMs: Long?, val toleranceMinutes: Int) : SearchRoutesAction()
    data object OnSearchAnyTime : SearchRoutesAction()
    data class OnTripClick(val tripId: String) : SearchRoutesAction()
    data object RetryLoad : SearchRoutesAction()
}
