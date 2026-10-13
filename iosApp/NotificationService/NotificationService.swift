import UserNotifications

/// Rewrites each push before iOS shows it, so its text is rendered on the device in the device's
/// language and time zone, like PushNotificationRenderer on Android. The backend's generic
/// localized alert stays as the fallback for anything this can't or shouldn't render.
final class NotificationService: UNNotificationServiceExtension {
    override func didReceive(
        _ request: UNNotificationRequest,
        withContentHandler contentHandler: @escaping (UNNotificationContent) -> Void
    ) {
        // Rendering is synchronous and only reads bundled strings, so the content is handed back
        // right away and serviceExtensionTimeWillExpire never has pending work.
        let userInfo = request.content.userInfo
        guard
            // A push for someone other than the signed-in user (a stale token after a sign-out)
            // keeps the generic text: without a filtering entitlement iOS shows it anyway, so
            // this is the closest to Android dropping it.
            let recipientId = userInfo["recipientId"] as? String,
            recipientId == SignedInUserStore.userId,
            let text = NotificationText(userInfo: userInfo),
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
