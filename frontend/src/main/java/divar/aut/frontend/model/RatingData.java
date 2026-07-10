package divar.aut.frontend.model;

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
