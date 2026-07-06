package divar.aut.backend.dto;

import divar.aut.backend.entity.Message;

import java.time.LocalDateTime;

public class MessageResponse {
    private final Long id;
    private final Long conversationId;
    private final Long senderId;
    private final String senderUsername;
    private final String content;
    private final LocalDateTime sentAt;

    public MessageResponse(Message message) {
        this.id = message.getId();
        this.conversationId = message.getConversation().getId();
        this.senderId = message.getSender().getId();
        this.senderUsername = message.getSender().getUsername();
        this.content = message.getContent();
        this.sentAt = message.getSentAt();
    }

    public Long getId() { return id; }
    public Long getConversationId() { return conversationId; }
    public Long getSenderId() { return senderId; }
    public String getSenderUsername() { return senderUsername; }
    public String getContent() { return content; }
    public LocalDateTime getSentAt() { return sentAt; }
}
