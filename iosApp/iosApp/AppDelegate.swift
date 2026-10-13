import ComposeApp
import FirebaseCore
import FirebaseMessaging
import GoogleMaps
import UIKit
import UserNotifications

/// Starts the platform SDKs and the shared Kotlin app before the first scene appears: the iOS
/// counterpart of CarpoolApplication on Android.
final class AppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        // Reads GoogleService-Info.plist; the Kotlin Firebase SDK uses this default app.
        FirebaseApp.configure()
        GMSServices.provideAPIKey(IosApp.shared.mapsApiKey)
        IosApp.shared.start(mapViewFactory: GoogleMapViewFactory())

        // Set before launch finishes so the tap that launched the app is delivered.
        UNUserNotificationCenter.current().delegate = self
        Messaging.messaging().delegate = self
        // Registering needs no permission. The alert permission is asked right after a meaningful
        // action (rememberNotificationPermissionState), never at launch.
        application.registerForRemoteNotifications()
        return true
    }
}
