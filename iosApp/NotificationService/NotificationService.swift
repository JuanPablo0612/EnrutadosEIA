import UserNotifications

/// Rewrites each push before iOS shows it, so its text is rendered on the device in the device's
/// language and time zone, like PushNotificationRenderer on Android. The backend's generic
/// localized alert stays as the fallback for anything this can't render.
final class NotificationService: UNNotificationServiceExtension {
    override func didReceive(
        _ request: UNNotificationRequest,
        withContentHandler contentHandler: @escaping (UNNotificationContent) -> Void
    ) {
        // Rendering is synchronous and only reads bundled strings, so the content is handed back
        // right away and serviceExtensionTimeWillExpire never has pending work.
        guard
            let text = NotificationText(userInfo: request.content.userInfo),
            let content = request.content.mutableCopy() as? UNMutableNotificationContent
        else {
            contentHandler(request.content)
            return
        }
        content.title = text.title
        content.body = text.body
        contentHandler(content)
    }
}
