import Foundation

/// A push's title and body, rendered the way resolveNotificationText renders it in the app
/// (composeApp/src/commonMain/.../presentation/notification/NotificationText.kt): same kinds, same
/// strings and fallbacks. The strings come from iosApp/Shared/Localizable.xcstrings, whose keys
/// and texts mirror composeResources. Change both together.
struct NotificationText {
    let title: String
    let body: String

    /// Reads the data keys the backend puts next to `aps` (functions/src/notifications/notify.ts).
    /// Returns nil for a push that isn't the backend's, which is then shown as sent.
    init?(userInfo: [AnyHashable: Any]) {
        guard userInfo["v"] != nil, let type = userInfo["type"] as? String, !type.isEmpty else {
            return nil
        }
        var params: [String: String] = [:]
        for (key, value) in userInfo {
            if let key = key as? String, let value = value as? String {
                params[key] = value
            }
        }
        let rendered = Self.render(type: type, params: params)
        title = rendered.title
        body = rendered.body
    }

    private static func render(type: String, params: [String: String]) -> (title: String, body: String) {
        func name(_ key: String) -> String {
            nonBlank(params[key]) ?? localized("notification_someone")
        }
        let origin = params[Param.originName] ?? ""
        let destination = params[Param.destinationName] ?? ""
        let departure = formatDeparture(params[Param.departureTime])

        func text(_ title: String, _ body: String, who: String) -> (title: String, body: String) {
            (localized(title), localized(body, name(who), origin, destination, departure))
        }

        switch type {
        case "new_booking_request":
            return text(
                "notification_new_booking_request_title",
                "notification_new_booking_request_body_fmt",
                who: Param.passengerName
            )
        case "booking_accepted":
            return text(
                "notification_booking_accepted_title",
                "notification_booking_accepted_body_fmt",
                who: Param.driverName
            )
        case "booking_rejected":
            let driver = name(Param.driverName)
            let body: String
            if let reason = rejectReasonText(params[Param.rejectReason]) {
                body = localized(
                    "notification_booking_rejected_reason_body_fmt",
                    driver, origin, destination, departure, reason
                )
            } else {
                body = localized("notification_booking_rejected_body_fmt", driver, origin, destination, departure)
            }
            return (localized("notification_booking_rejected_title"), body)
        case "booking_cancelled_by_passenger":
            return text(
                "notification_booking_cancelled_by_passenger_title",
                "notification_booking_cancelled_by_passenger_body_fmt",
                who: Param.passengerName
            )
        case "booking_cancelled_by_driver":
            return text(
                "notification_booking_cancelled_by_driver_title",
                "notification_booking_cancelled_by_driver_body_fmt",
                who: Param.driverName
            )
        case "trip_cancelled":
            return text("notification_trip_cancelled_title", "notification_trip_cancelled_body_fmt", who: Param.driverName)
        case "trip_started":
            return text("notification_trip_started_title", "notification_trip_started_body_fmt", who: Param.driverName)
        case "trip_completed":
            return text("notification_trip_completed_title", "notification_trip_completed_body_fmt", who: Param.driverName)
        case "trip_updated":
            return text("notification_trip_updated_title", "notification_trip_updated_body_fmt", who: Param.driverName)
        case "new_message":
            return (
                localized("notification_new_message_title_fmt", name(Param.senderName)),
                params[Param.messagePreview] ?? ""
            )
        default:
            // A kind this build doesn't know (added by a newer backend).
            return (localized("notification_generic_title"), localized("notification_generic_body"))
        }
    }

    /// "dd/MM h:mm AM" in the device's time zone, or "" when missing; the app's own format.
    private static func formatDeparture(_ epochMs: String?) -> String {
        guard let millis = epochMs.flatMap({ Int64($0) }), millis > 0 else { return "" }
        let date = Date(timeIntervalSince1970: TimeInterval(millis) / 1000)
        // Gregorian like kotlinx-datetime, whatever calendar the device uses.
        var calendar = Calendar(identifier: .gregorian)
        calendar.timeZone = .current
        let parts = calendar.dateComponents([.day, .month, .hour, .minute], from: date)
        let hour = parts.hour ?? 0
        let hour12 = hour % 12 == 0 ? 12 : hour % 12
        let marker = localized(hour < 12 ? "time_am" : "time_pm")
        return String(
            format: "%02ld/%02ld %ld:%02ld %@",
            parts.day ?? 0, parts.month ?? 0, hour12, parts.minute ?? 0, marker
        )
    }

    private static func rejectReasonText(_ key: String?) -> String? {
        switch key {
        case "TRIP_FULL": return localized("reject_reason_trip_full")
        case "TRIP_CANCELLED": return localized("reject_reason_trip_cancelled")
        case "PICKUP_NOT_POSSIBLE": return localized("reject_reason_pickup_not_possible")
        case "OTHER": return localized("reject_reason_other")
        default: return nil
        }
    }

    private static func localized(_ key: String, _ arguments: CVarArg...) -> String {
        let format = Bundle.main.localizedString(forKey: key, value: nil, table: nil)
        return arguments.isEmpty ? format : String(format: format, locale: .current, arguments: arguments)
    }

    private static func nonBlank(_ value: String?) -> String? {
        guard let value, !value.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else { return nil }
        return value
    }
}

/// Keys of the data the backend sends; mirrors NotificationParams.kt.
private enum Param {
    static let originName = "originName"
    static let destinationName = "destinationName"
    static let departureTime = "departureTime"
    static let driverName = "driverName"
    static let passengerName = "passengerName"
    static let rejectReason = "rejectReason"
    static let senderName = "senderName"
    static let messagePreview = "messagePreview"
}
