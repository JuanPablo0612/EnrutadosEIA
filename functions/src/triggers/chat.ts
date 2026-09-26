import {getFirestore} from "firebase-admin/firestore";
import {onDocumentCreated} from "firebase-functions/v2/firestore";
import {Collections} from "../config";
import {BookingDoc, MessageDoc} from "../model";
import {str, userName} from "../notifications/params";
import {notify} from "../notifications/notify";

const MAX_PREVIEW_LENGTH = 120;

/**
 * A chat message was sent: pushes it to the other party of the booking.
 * Push only, no in-app notification, since the chat keeps its own unread
 * state.
 */
export const onChatMessageCreated = onDocumentCreated(
  `${Collections.chats}/{bookingId}/${Collections.messages}/{messageId}`,
  async (event) => {
    const message = event.data?.data() as MessageDoc | undefined;
    if (!message) return;
    const bookingId = event.params.bookingId;
    const bookingSnap = await getFirestore()
      .collection(Collections.bookings)
      .doc(bookingId)
      .get();
    const booking = bookingSnap.data() as BookingDoc | undefined;
    if (!booking) return;

    const senderId = str(message.senderId, 200);
    const passengerId = str(booking.passengerId, 200);
    const driverId = str(booking.driverId, 200);
    const senderIsPassenger = senderId === passengerId;
    const senderName = senderIsPassenger ?
      str(booking.passengerName) : await userName(driverId);

    await notify({
      recipientId: senderIsPassenger ? driverId : passengerId,
      type: "new_message",
      params: {
        bookingId,
        tripId: str(booking.tripId, 200),
        senderId,
        senderName,
        messagePreview: str(message.text, MAX_PREVIEW_LENGTH),
      },
    });
  },
);
