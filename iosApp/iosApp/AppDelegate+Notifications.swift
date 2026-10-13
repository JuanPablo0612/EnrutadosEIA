import ComposeApp
import FirebaseMessaging
import OSLog
import UIKit
import UserNotifications

private let logger = Logger(subsystem: Bundle.main.bundleIdentifier ?? "EnrutadosEIA", category: "Push")

// FCM's method swizzling is off (FirebaseAppDelegateProxyEnabled = NO in Info.plist), as FCM asks
// of SwiftUI apps, so the APNs token and each delivered message are handed to FCM here.
extension AppDelegate {
    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        Messaging.messaging().apnsToken = deviceToken
    }

    func application(
        _ application: UIApplication,
        didFailToRegisterForRemoteNotificationsWithError error: Error
    ) {
        // Pushes stay off until the next launch registers again.
        logger.error("APNs registration failed: \(error.localizedDescription, privacy: .public)")
    }
}

extension AppDelegate: UNUserNotificationCenterDelegate {
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        let userInfo = notification.request.content.userInfo
        Messaging.messaging().appDidReceiveMessage(userInfo)
        let shouldPresent = IosPushNotifications.shared.shouldPresentInForeground(userInfo: userInfo)
        completionHandler(shouldPresent ? [.banner, .list, .sound] : [])
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        let userInfo = response.notification.request.content.userInfo
        Messaging.messaging().appDidReceiveMessage(userInfo)
        if response.actionIdentifier == UNNotificationDefaultActionIdentifier {
            IosPushNotifications.shared.onNotificationTapped(userInfo: userInfo)
        }
        completionHandler()
    }
}

extension AppDelegate: MessagingDelegate {
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        guard fcmToken != nil else { return }
        IosPushNotifications.shared.onNewToken()
    }
}
