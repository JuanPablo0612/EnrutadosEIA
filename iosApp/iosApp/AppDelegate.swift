import ComposeApp
import FirebaseAuth
import FirebaseCore
import FirebaseMessaging
import GoogleMaps
import UIKit
import UserNotifications

/// Starts the platform SDKs and the shared Kotlin app before the first scene appears: the iOS
/// counterpart of CarpoolApplication on Android.
final class AppDelegate: NSObject, UIApplicationDelegate {
    private var authStateListener: AuthStateDidChangeListenerHandle?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        // Reads GoogleService-Info.plist; the Kotlin Firebase SDK uses this default app.
        FirebaseApp.configure()
        // Every sign-in and sign-out, forced ones included, so the NotificationService extension
        // knows whose pushes it may render.
        authStateListener = Auth.auth().addStateDidChangeListener { _, user in
            SignedInUserStore.userId = user?.uid
        }
        let mapsApiKey = IosApp.shared.mapsApiKey
        // An empty key would make the first map view throw; the shared UI shows no map instead.
        if !mapsApiKey.isEmpty {
            GMSServices.provideAPIKey(mapsApiKey)
        }
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
