import ComposeApp
import GoogleMaps
import UIKit

/// The map behind MapPreview: one draggable pin and tappable points of interest, matching
/// MapPreview.android.kt.
final class PlaceMapView: UIView, NativePlaceMap {
    private static let zoom: Float = 16

    private let mapView = GMSMapView(options: GMSMapViewOptions())
    private let listener: NativePlaceMapListener
    private let pin = GMSMarker()
    private var shownCoordinates: Coordinates?
    private var hasPlacedCamera = false

    init(listener: NativePlaceMapListener) {
        self.listener = listener
        super.init(frame: .zero)
        pin.isDraggable = true
        mapView.delegate = self
        // Off as on Android: the screens have their own "use my location" action.
        mapView.settings.myLocationButton = false
        mapView.frame = bounds
        mapView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        addSubview(mapView)
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) is not supported")
    }

    var view: UIView { self }

    func showPin(coordinates: Coordinates) {
        // Compose calls this on every recomposition; only a new position moves anything, so a map
        // the user panned away stays where they left it.
        guard coordinates != shownCoordinates else { return }
        shownCoordinates = coordinates

        let target = CLLocationCoordinate2D(latitude: coordinates.latitude, longitude: coordinates.longitude)
        pin.position = target
        pin.map = mapView

        let camera = GMSCameraPosition(target: target, zoom: Self.zoom)
        if hasPlacedCamera {
            mapView.animate(to: camera)
        } else {
            mapView.camera = camera
            hasPlacedCamera = true
        }
    }

    func hidePin() {
        shownCoordinates = nil
        pin.map = nil
    }

    func showMyLocation(enabled: Bool) {
        if mapView.isMyLocationEnabled != enabled {
            mapView.isMyLocationEnabled = enabled
        }
    }
}

extension PlaceMapView: GMSMapViewDelegate {
    func mapView(_ mapView: GMSMapView, didEndDragging marker: GMSMarker) {
        listener.onPinDragged(coordinates: coordinates(of: marker.position))
    }

    func mapView(
        _ mapView: GMSMapView,
        didTapPOIWithPlaceID placeID: String,
        name: String,
        location: CLLocationCoordinate2D
    ) {
        listener.onPoiTapped(
            poi: MapPointOfInterest(placeId: placeID, name: name, coordinates: coordinates(of: location))
        )
    }
}

private func coordinates(of position: CLLocationCoordinate2D) -> Coordinates {
    Coordinates(latitude: position.latitude, longitude: position.longitude)
}
