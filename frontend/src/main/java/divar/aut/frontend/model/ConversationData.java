package divar.aut.frontend.model;

public record ConversationData(
        Long id,
        Long adId,
        String adTitle,
        Long buyerId,
        String buyerUsername,
        Long sellerId,
        String sellerUsername,
        String createdAt,
        String lastMessagePreview,
        String lastMessageAt
) {
}
