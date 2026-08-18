package com.juanpablo0612.carpool.presentation.route.search

import com.juanpablo0612.carpool.domain.place.model.Place

sealed class SearchRoutesAction {
    data object OnPickOrigin : SearchRoutesAction()
    data object OnPickDestination : SearchRoutesAction()
    data class OnPlaceSelected(val place: Place) : SearchRoutesAction()
    data object OnCancelPlaceSelection : SearchRoutesAction()
    data object OnSwapPlaces : SearchRoutesAction()
    data class OnDateTimeChanged(val epochMs: Long?, val toleranceMinutes: Int) : SearchRoutesAction()
    data object OnSearchClick : SearchRoutesAction()
    data class OnFiltersChanged(val filters: SearchFilters) : SearchRoutesAction()
    data object OnShowFilters : SearchRoutesAction()
    data object OnDismissFilters : SearchRoutesAction()
    data object OnShowDateTimeSheet : SearchRoutesAction()
    data object OnDismissDateTimeSheet : SearchRoutesAction()
    data class OnTripClick(val tripId: String) : SearchRoutesAction()
}
