package divar.aut.backend.dto;

import divar.aut.backend.entity.Conversation;
import divar.aut.backend.entity.Message;

import java.time.LocalDateTime;

public class ConversationResponse {
    private final Long id;
    private final Long adId;
    private final String adTitle;
    private final Long buyerId;
    private final String buyerUsername;
    private final Long sellerId;
    private final String sellerUsername;
    private final LocalDateTime createdAt;
    private final String lastMessagePreview;
    private final LocalDateTime lastMessageAt;

    public ConversationResponse(Conversation conversation, Message lastMessageOrNull) {
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
}
