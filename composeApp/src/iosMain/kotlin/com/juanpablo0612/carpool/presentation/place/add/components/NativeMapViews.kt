package com.juanpablo0612.carpool.presentation.place.add.components

import com.juanpablo0612.carpool.domain.place.model.Coordinates
import com.juanpablo0612.carpool.domain.place.model.MapPointOfInterest
import platform.UIKit.UIColor
import platform.UIKit.UIView

/**
 * Google Maps views built in Swift (iosApp/iosApp/Maps/), where the Maps SDK for iOS is linked as a
 * Swift package; Kotlin can't call it without a cinterop of its own. Google Maps rather than MapKit
 * because Places API results shown on a map must be shown on a Google map. Registered in Koin by
 * `IosApp.start`.
 */
interface NativeMapViewFactory {
    fun createPlaceMap(listener: NativePlaceMapListener): NativePlaceMap
    fun createRouteMap(): NativeRouteMap
}

/** The map behind [MapPreview]: one draggable pin, tappable points of interest. */
interface NativePlaceMap {
    val view: UIView

    /** Moves the pin and camera to [coordinates]; the first call jumps, later ones animate. */
    fun showPin(coordinates: Coordinates)

    fun hidePin()

    fun showMyLocation(enabled: Boolean)
}

interface NativePlaceMapListener {
    fun onPinDragged(coordinates: Coordinates)

    fun onPoiTapped(poi: MapPointOfInterest)
}

/** The read-only map behind [MapRoutePreview]. */
interface NativeRouteMap {
    val view: UIView

    /** Pins every stop, joins them with a [lineColor] line and frames them all. */
    fun showRoute(stops: List<Coordinates>, lineColor: UIColor)
}
