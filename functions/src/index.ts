import {initializeApp} from "firebase-admin/app";
import {setGlobalOptions} from "firebase-functions";
import {REGION} from "./config";

initializeApp();

// Caps concurrent containers per function to contain unexpected traffic spikes.
setGlobalOptions({region: REGION, maxInstances: 10});

export {onBookingCreated, onBookingUpdated} from "./triggers/bookings";
export {onFcmTokenCreated} from "./triggers/fcmTokens";
export {onTripDeleted, onTripUpdated} from "./triggers/trips";
export {onChatMessageCreated} from "./triggers/chat";
export {onRatingCreated} from "./triggers/ratings";
export {deleteAccount, onAuthUserDeleted} from "./account/deleteAccount";
