package divar.aut.backend.dto;

import divar.aut.backend.entity.SellerRating;

import java.time.LocalDateTime;

public class RatingResponse {
    private final Long id;
    private final Long sellerId;
    private final Long buyerId;
    private final String buyerUsername;
    private final Long adId;
    private final int score;
    private final String comment;
    private final LocalDateTime createdAt;

    public RatingResponse(SellerRating rating) {
        this.id = rating.getId();
        this.sellerId = rating.getSeller().getId();
        this.buyerId = rating.getBuyer().getId();
        this.buyerUsername = rating.getBuyer().getUsername();
        this.adId = rating.getAd().getId();
        this.score = rating.getScore();
        this.comment = rating.getComment();
        this.createdAt = rating.getCreatedAt();
    }

    public Long getId() { return id; }
    public Long getSellerId() { return sellerId; }
    public Long getBuyerId() { return buyerId; }
    public String getBuyerUsername() { return buyerUsername; }
    public Long getAdId() { return adId; }
    public int getScore() { return score; }
    public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
