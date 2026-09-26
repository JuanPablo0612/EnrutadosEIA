import {getFirestore} from "firebase-admin/firestore";
import {BatchResponse} from "firebase-admin/messaging";
import {Collections} from "../config";

/** FCM error codes meaning the token will never work again. */
const DEAD_TOKEN_CODES = new Set([
  "messaging/registration-token-not-registered",
  "messaging/invalid-registration-token",
]);

/**
 * Returns the FCM tokens registered for a user.
 * @param {string} uid the user id
 * @return {Promise<string[]>} the tokens (document ids)
 */
export async function getTokens(uid: string): Promise<string[]> {
  const snap = await getFirestore()
    .collection(Collections.users)
    .doc(uid)
    .collection(Collections.fcmTokens)
    .get();
  return snap.docs.map((doc) => doc.id);
}

/**
 * Deletes the tokens a multicast send reported as permanently invalid.
 * @param {string} uid the user the tokens belong to
 * @param {BatchResponse} response the multicast result
 * @param {string[]} tokens the tokens, in the same order as the send
 */
export async function pruneDeadTokens(
  uid: string,
  response: BatchResponse,
  tokens: string[],
): Promise<void> {
  const tokensRef = getFirestore()
    .collection(Collections.users)
    .doc(uid)
    .collection(Collections.fcmTokens);
  const deletions = response.responses
    .map((result, index) => ({result, token: tokens[index]}))
    .filter(({result}) =>
      !result.success && DEAD_TOKEN_CODES.has(result.error?.code ?? ""))
    .map(({token}) => tokensRef.doc(token).delete());
  await Promise.all(deletions);
}
