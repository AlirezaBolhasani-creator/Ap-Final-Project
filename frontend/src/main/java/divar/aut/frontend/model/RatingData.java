package divar.aut.frontend.model;

/**
 * Data Transfer Object representing a rating given by a buyer to a seller.
 * <p>
 * Contains details about the rating, including the score, optional comment,
 * the advertisement context, and timestamps. Used for displaying seller
 * ratings and comments in the UI.
 * </p>
 *
 * @param id             the unique identifier of the rating.
 * @param sellerId       the ID of the seller being rated.
 * @param buyerId        the ID of the buyer who submitted the rating.
 * @param buyerUsername  the username of the buyer who submitted the rating.
 * @param adId           the ID of the advertisement associated with this rating.
 * @param score          the rating score (1–5).
 * @param comment        the optional textual comment provided by the buyer.
 * @param createdAt      the timestamp when the rating was created (ISO‑8601 format).
 */
public record RatingData(
        Long id,
        Long sellerId,
        Long buyerId,
        String buyerUsername,
        Long adId,
        int score,
        String comment,
        String createdAt
) {
}