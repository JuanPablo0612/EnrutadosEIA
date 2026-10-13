import ComposeApp
import Foundation

/// Builds the Google Maps views the shared Compose UI embeds (NativeMapViews.kt).
final class GoogleMapViewFactory: NSObject, NativeMapViewFactory {
    func createPlaceMap(listener: NativePlaceMapListener) -> NativePlaceMap {
        PlaceMapView(listener: listener)
    }

    func createRouteMap() -> NativeRouteMap {
        RouteMapView()
    }
}
