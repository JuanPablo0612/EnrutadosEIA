import Foundation

/// The signed-in user's id, shared with the NotificationService extension through the app group,
/// so the extension only renders pushes meant for that user (Android drops the rest in
/// CarpoolMessagingService). Both targets compile this file.
enum SignedInUserStore {
    private static let userIdKey = "signedInUserId"

    // Without the app group (missing from Info.plist or the entitlements, as in a Personal Team
    // build) the two processes don't share this suite, so the extension reads no user and keeps
    // every push's generic text.
    private static let defaults: UserDefaults? =
        (Bundle.main.object(forInfoDictionaryKey: "AppGroupIdentifier") as? String)
            .flatMap { UserDefaults(suiteName: $0) }

    static var userId: String? {
        get { defaults?.string(forKey: userIdKey) }
        set { defaults?.set(newValue, forKey: userIdKey) }
    }
}
