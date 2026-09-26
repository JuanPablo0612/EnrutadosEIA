import {getAuth} from "firebase-admin/auth";
import {HttpsError, onCall} from "firebase-functions/v2/https";
import * as functionsV1 from "firebase-functions/v1";
import {REGION} from "../config";
import {purgeUserData} from "./purgeUserData";

/**
 * Deletes the caller's account: purges their data first (while their
 * profile still exists, so notifications can name them) and deletes the
 * auth user last, so a failure part-way can be retried. Unlike the client
 * SDK's user.delete(), this does not require a recent sign-in.
 */
export const deleteAccount = onCall(
  {timeoutSeconds: 300, memory: "512MiB"},
  async (request) => {
    const uid = request.auth?.uid;
    if (!uid) {
      throw new HttpsError("unauthenticated", "Sign in to delete an account.");
    }
    await purgeUserData(uid);
    try {
      await getAuth().deleteUser(uid);
    } catch (error) {
      if ((error as {code?: string}).code !== "auth/user-not-found") {
        throw error;
      }
    }
    return {status: "deleted"};
  },
);

/**
 * Safety net for accounts deleted outside the app (console, Admin SDK).
 * Auth deletion events only exist as 1st-gen triggers. Purging twice is
 * harmless.
 */
export const onAuthUserDeleted = functionsV1
  .region(REGION)
  .runWith({timeoutSeconds: 300})
  .auth.user()
  .onDelete((user) => purgeUserData(user.uid));
