import ComposeApp
import GoogleMaps
import UIKit

/// The read-only map behind MapRoutePreview: every stop pinned, joined by a line and framed,
/// matching MapRoutePreview.android.kt.
final class RouteMapView: UIView, NativeRouteMap {
    // Android draws a 6 px line and frames with 80 px of padding; on a 3x screen that is 2 and
    // about 27 points.
    private static let lineWidth: CGFloat = 2
    private static let framePadding: CGFloat = 27
    private static let singleStopZoom: Float = 14

    private let mapView = GMSMapView(options: GMSMapViewOptions())
    private var shownStops: [Coordinates] = []
    private var shownLineColor: UIColor?
    private var needsFraming = false

    override init(frame: CGRect) {
        super.init(frame: frame)
        mapView.settings.setAllGesturesEnabled(false)
        mapView.frame = bounds
        mapView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        addSubview(mapView)
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) is not supported")
    }

    var view: UIView { self }

    func showRoute(stops: [Coordinates], lineColor: UIColor) {
        // Compose calls this on every recomposition; redraw only when the route or theme changed.
        guard stops != shownStops || lineColor != shownLineColor else { return }
        let stopsChanged = stops != shownStops
        shownStops = stops
        shownLineColor = lineColor

        mapView.clear()
        let path = GMSMutablePath()
        for stop in stops {
            let position = CLLocationCoordinate2D(latitude: stop.latitude, longitude: stop.longitude)
            path.add(position)
            GMSMarker(position: position).map = mapView
        }
        if stops.count >= 2 {
            let line = GMSPolyline(path: path)
            line.strokeColor = lineColor
            line.strokeWidth = Self.lineWidth
            line.map = mapView
        }

        if stopsChanged {
            needsFraming = true
            setNeedsLayout()
        }
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        frameStopsIfNeeded()
    }

    // Fitting the camera needs the view's real size, which is only known after layout.
    private func frameStopsIfNeeded() {
        guard needsFraming, !bounds.isEmpty, let first = shownStops.first else { return }
        needsFraming = false

        if shownStops.count == 1 {
            let target = CLLocationCoordinate2D(latitude: first.latitude, longitude: first.longitude)
            mapView.animate(to: GMSCameraPosition(target: target, zoom: Self.singleStopZoom))
        } else {
            let path = GMSMutablePath()
            for stop in shownStops {
                path.add(CLLocationCoordinate2D(latitude: stop.latitude, longitude: stop.longitude))
            }
            let bounds = GMSCoordinateBounds(path: path)
            mapView.animate(with: GMSCameraUpdate.fit(bounds, withPadding: Self.framePadding))
        }
    }
}
