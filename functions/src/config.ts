/**
 * Deployment region. Must match the Firestore database location (nam5 →
 * us-central1) and `FUNCTIONS_REGION` in the app's BackendConfig.kt.
 */
export const REGION = "us-central1";

export const Collections = {
  users: "users",
  fcmTokens: "fcmTokens",
  trips: "trips",
  bookings: "bookings",
  notifications: "notifications",
  notificationItems: "items",
  chats: "chats",
  messages: "messages",
  ratings: "ratings",
  routes: "routes",
  vehicles: "vehicles",
  places: "places",
} as const;

/** How long FCM keeps an undelivered push, per notification kind. */
export const PushTtlMs = {
  chat: 60 * 60 * 1000,
  tripStarted: 30 * 60 * 1000,
  default: 24 * 60 * 60 * 1000,
} as const;
