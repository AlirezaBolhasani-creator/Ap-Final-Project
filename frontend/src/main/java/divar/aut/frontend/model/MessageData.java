package divar.aut.frontend.model;

/**
 * Data Transfer Object representing a message within a conversation.
 * <p>
 * Contains all information about a single message, including sender details,
 * content, timestamps, and metadata such as read status and admin flag.
 * </p>
 *
 * @param id              the unique identifier of the message.
 * @param conversationId  the ID of the conversation this message belongs to.
 * @param senderId        the ID of the user who sent the message.
 * @param senderUsername  the username of the sender.
 * @param content         the text content of the message.
 * @param sentAt          the timestamp when the message was sent (ISO‑8601 format).
 * @param senderAdmin     flag indicating whether the sender has administrator privileges.
 * @param read            flag indicating whether the message has been read by the recipient.
 */
public record MessageData(
        Long id,
        Long conversationId,
        Long senderId,
        String senderUsername,
        String content,
        String sentAt,
        boolean senderAdmin,
        boolean read
) {
}