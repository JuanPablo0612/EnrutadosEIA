import {getFirestore} from "firebase-admin/firestore";
import {onDocumentCreated} from "firebase-functions/v2/firestore";
import {Collections} from "../config";

/**
 * A device registered its FCM token for a user: removes the same token from
 * any other user, so a phone shared between accounts only receives pushes
 * for whoever signed in last.
 */
export const onFcmTokenCreated = onDocumentCreated(
  `${Collections.users}/{uid}/${Collections.fcmTokens}/{token}`,
  async (event) => {
    const {uid, token} = event.params;
    const duplicates = await getFirestore()
      .collectionGroup(Collections.fcmTokens)
      .where("token", "==", token)
      .get();
    await Promise.all(
      duplicates.docs
        .filter((doc) => doc.ref.parent.parent?.id !== uid)
        .map((doc) => doc.ref.delete()),
    );
  },
);
