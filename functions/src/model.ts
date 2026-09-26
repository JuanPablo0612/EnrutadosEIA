/**
 * Firestore document shapes as the Android app writes them (see the
 * `*Dto.kt` classes under `data/`). Fields are optional because a trigger
 * must never crash on a malformed or partially written document.
 */

export type BookingStatus = "PENDING" | "CONFIRMED" | "REJECTED" | "CANCELLED";
export type TripStatus = "ACTIVE" | "IN_PROGRESS" | "COMPLETED" | "CANCELLED";

export interface PlaceDoc {
  name?: string;
  address?: string;
}

export interface BookingDoc {
  tripId?: string;
  passengerId?: string;
  driverId?: string;
  passengerName?: string;
  originName?: string;
  destinationName?: string;
  departureTime?: number;
  status?: BookingStatus;
  rejectReason?: string | null;
  /** A user id, or "system" when the backend made the change. */
  cancelledBy?: string;
}

export interface TripDoc {
  driverId?: string;
  origin?: PlaceDoc;
  destination?: PlaceDoc;
  departureTime?: number;
  status?: TripStatus;
  seatCount?: number;
  confirmedSeats?: number;
}

export interface UserDoc {
  name?: string | null;
  ratingSum?: number;
  ratingCount?: number;
}

export interface RatingDoc {
  bookingId?: string;
  raterId?: string;
  rateeId?: string;
  stars?: number;
  aggregatedAt?: number;
  aggregation?: string;
}

export interface MessageDoc {
  senderId?: string;
  text?: string;
}

/** Marks a change made by the backend rather than by a user. */
export const SYSTEM_ACTOR = "system";

/**
 * Notification kinds. The app maps each key to localized text and a deep
 * link (NotificationType.kt), so keep both lists in sync.
 */
export type NotificationType =
  | "new_booking_request"
  | "booking_accepted"
  | "booking_rejected"
  | "booking_cancelled_by_passenger"
  | "booking_cancelled_by_driver"
  | "trip_cancelled"
  | "trip_started"
  | "trip_completed"
  | "new_message";

/**
 * Structured values the app renders into localized text. Always strings, so
 * the same map decodes as Map<String, String> in Kotlin and doubles as the
 * FCM data payload. Key names mirror NotificationParams.kt.
 */
export type NotificationParams = Record<string, string>;
