package divar.aut.backend.dto;

import divar.aut.backend.entity.Message;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for message responses.
 * <p>
 * Contains the full details of a message, including sender information,
 * content, timestamps, and a flag indicating if the sender is an admin.
 * </p>
 */
public class MessageResponse {
    private final Long id;
    private final Long conversationId;
    private final Long senderId;
    private final String senderUsername;
    private final String content;
    private final LocalDateTime sentAt;
    /**
     * Flag indicating whether the sender has administrator privileges.
     */
    private final boolean senderAdmin;

    /**
     * Constructs a MessageResponse from a Message entity.
     *
     * @param message the message entity.
     */
    private final boolean read;
    public MessageResponse(Message message) {
        this.id = message.getId();
        this.conversationId = message.getConversation().getId();
        this.senderId = message.getSender().getId();
        this.senderUsername = message.getSender().getUsername();
        this.content = message.getContent();
        this.sentAt = message.getSentAt();
        this.senderAdmin = message.getSender().isAdmin();
        this.read = message.isRead();
    }

    public Long getId() { return id; }
    public Long getConversationId() { return conversationId; }
    public Long getSenderId() { return senderId; }
    public String getSenderUsername() { return senderUsername; }
    public String getContent() { return content; }
    public LocalDateTime getSentAt() { return sentAt; }
    public boolean isSenderAdmin() { return senderAdmin; }
    public boolean isRead() { return read; }
}