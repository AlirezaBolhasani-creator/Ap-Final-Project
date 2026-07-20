package divar.aut.backend.dto;

import divar.aut.backend.entity.SellerRating;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for rating responses.
 * <p>
 * Contains full details of a seller rating, including the rating ID,
 * seller/buyer information, the advertisement context, score, comment,
 * and timestamp.
 * </p>
 */
public class RatingResponse {

    /**
     * The unique identifier of the rating.
     */
    private final Long id;

    /**
     * The ID of the seller being rated.
     */
    private final Long sellerId;

    /**
     * The ID of the buyer who submitted the rating.
     */
    private final Long buyerId;

    /**
     * The username of the buyer who submitted the rating.
     */
    private final String buyerUsername;

    /**
     * The ID of the advertisement associated with this rating.
     */
    private final Long adId;

    /**
     * The rating score (1-5).
     */
    private final int score;

    /**
     * The optional comment provided by the buyer.
     */
    private final String comment;

    /**
     * The timestamp when the rating was created.
     */
    private final LocalDateTime createdAt;

    /**
     * Constructs a RatingResponse from a SellerRating entity.
     *
     * @param rating the seller rating entity.
     */
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