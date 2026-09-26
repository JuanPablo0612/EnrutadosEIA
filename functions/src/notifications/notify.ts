import {getFirestore} from "firebase-admin/firestore";
import {getMessaging} from "firebase-admin/messaging";
import * as logger from "firebase-functions/logger";
import {Collections, PushTtlMs} from "../config";
import {NotificationParams, NotificationType} from "../model";
import {getTokens, pruneDeadTokens} from "./tokens";

/** gRPC ALREADY_EXISTS: the notification was delivered by an earlier run. */
const ALREADY_EXISTS = 6;

export interface NotifyRequest {
  recipientId: string;
  type: NotificationType;
  params: NotificationParams;
  /**
   * Deterministic id of the in-app notification document. Omit to send a
   * push only (chat messages, which have their own unread state).
   */
  inAppId?: string;
}

/**
 * Stores an in-app notification (if [NotifyRequest.inAppId] is set) and
 * sends a data-only push the app renders in the device's language.
 *
 * Idempotent: if the in-app document already exists, a previous delivery of
 * the same event handled it and nothing is sent again. Never throws, so a
 * failed notification never fails the trigger that caused it.
 * @param {NotifyRequest} request who to notify and with what
 */
export async function notify(request: NotifyRequest): Promise<void> {
  const {recipientId, type, params, inAppId} = request;
  if (!recipientId) return;
  try {
    if (inAppId) {
      const created = await createInApp(recipientId, type, params, inAppId);
      if (!created) return;
    }
    await sendPush(recipientId, type, params, inAppId ?? "");
  } catch (error) {
    logger.error("notify failed", {recipientId, type, error});
  }
}

/**
 * Creates the in-app notification document.
 * @param {string} recipientId the user notified
 * @param {NotificationType} type the notification kind
 * @param {NotificationParams} params the render params
 * @param {string} id the deterministic document id
 * @return {Promise<boolean>} false when it already existed
 */
async function createInApp(
  recipientId: string,
  type: NotificationType,
  params: NotificationParams,
  id: string,
): Promise<boolean> {
  const ref = getFirestore()
    .collection(Collections.notifications)
    .doc(recipientId)
    .collection(Collections.notificationItems)
    .doc(id);
  try {
    // Plain epoch millis, not serverTimestamp(): the app decodes a Long.
    await ref.create({
      id,
      userId: recipientId,
      type,
      params,
      isRead: false,
      timestamp: Date.now(),
      schemaVersion: 2,
    });
    return true;
  } catch (error) {
    if ((error as {code?: number}).code === ALREADY_EXISTS) return false;
    throw error;
  }
}

/**
 * Sends a high-priority data-only push to every device of the recipient.
 * @param {string} recipientId the user notified
 * @param {NotificationType} type the notification kind
 * @param {NotificationParams} params the render params
 * @param {string} notificationId the in-app document id, or ""
 */
async function sendPush(
  recipientId: string,
  type: NotificationType,
  params: NotificationParams,
  notificationId: string,
): Promise<void> {
  const tokens = await getTokens(recipientId);
  if (tokens.length === 0) return;

  const data = {
    ...params,
    v: "1",
    type,
    recipientId,
    notificationId,
    sentAt: String(Date.now()),
  };
  const ttl = type === "new_message" ? PushTtlMs.chat :
    type === "trip_started" ? PushTtlMs.tripStarted : PushTtlMs.default;

  if (process.env.FUNCTIONS_EMULATOR === "true") {
    logger.info("push (emulator, not sent)", {recipientId, data});
    return;
  }

  const response = await getMessaging().sendEachForMulticast({
    tokens,
    data,
    android: {priority: "high", ttl},
  });
  if (response.failureCount > 0) {
    await pruneDeadTokens(recipientId, response, tokens);
  }
}
