package divar.aut.backend.dto;

import divar.aut.backend.entity.Conversation;
import divar.aut.backend.entity.Message;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for conversation responses.
 * <p>
 * Contains full conversation details including the associated advertisement,
 * buyer and seller information, and a preview of the last message.
 * </p>
 */
public class ConversationResponse {
    private final Long id;
    private final Long adId;
    private final String adTitle;
    private final Long buyerId;
    private final String buyerUsername;
    private final Long sellerId;
    private final String sellerUsername;
    private final LocalDateTime createdAt;

    /**
     * A preview (first few characters) of the last message in the conversation.
     * May be null if no messages exist.
     */
    private final String lastMessagePreview;

    /**
     * The timestamp of the last message in the conversation.
     * May be null if no messages exist.
     */
    private final LocalDateTime lastMessageAt;

    /**
     * Flag indicating whether the buyer has administrator privileges.
     */
    private final boolean buyerAdmin;

    /**
     * Flag indicating whether the seller has administrator privileges.
     */
    private final boolean sellerAdmin;

    /**
     * number of unread messages.
     */
    private final long unreadCount;

    /**
     * Constructs a ConversationResponse from a Conversation and its last message.
     *
     * @param conversation     the conversation entity.
     * @param lastMessageOrNull the last message in the conversation, or null if none exists.
     */
    public ConversationResponse(Conversation conversation, Message lastMessageOrNull, long unreadCount) {
        this.id = conversation.getId();
        this.adId = conversation.getAd().getId();
        this.adTitle = conversation.getAd().getTitle();
        this.buyerId = conversation.getBuyer().getId();
        this.buyerUsername = conversation.getBuyer().getUsername();
        this.sellerId = conversation.getSeller().getId();
        this.sellerUsername = conversation.getSeller().getUsername();
        this.createdAt = conversation.getCreatedAt();
        this.lastMessagePreview = lastMessageOrNull != null ? lastMessageOrNull.getContent() : null;
        this.lastMessageAt = lastMessageOrNull != null ? lastMessageOrNull.getSentAt() : null;
        this.buyerAdmin = conversation.getBuyer().isAdmin();
        this.sellerAdmin = conversation.getSeller().isAdmin();
        this.unreadCount = unreadCount;
    }

    public Long getId() { return id; }
    public Long getAdId() { return adId; }
    public String getAdTitle() { return adTitle; }
    public Long getBuyerId() { return buyerId; }
    public String getBuyerUsername() { return buyerUsername; }
    public Long getSellerId() { return sellerId; }
    public String getSellerUsername() { return sellerUsername; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getLastMessagePreview() { return lastMessagePreview; }
    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public boolean isBuyerAdmin() { return buyerAdmin; }
    public boolean isSellerAdmin() { return sellerAdmin; }
    public long getUnreadCount() { return unreadCount; }
}