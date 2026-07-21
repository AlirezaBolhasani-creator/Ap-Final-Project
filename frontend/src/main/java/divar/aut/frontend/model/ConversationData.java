package divar.aut.frontend.model;

/**
 * Data Transfer Object representing a conversation between a buyer and a seller.
 * <p>
 * Contains summary information about the conversation, including participants,
 * the associated advertisement, a preview of the last message, and metadata
 * such as unread counts and timestamps. Used for displaying conversation lists
 * in the UI.
 * </p>
 *
 * @param id                  the unique identifier of the conversation.
 * @param adId                the ID of the advertisement this conversation is about.
 * @param adTitle             the title of the advertisement.
 * @param buyerId             the ID of the buyer participant.
 * @param buyerUsername       the username of the buyer participant.
 * @param sellerId            the ID of the seller participant.
 * @param sellerUsername      the username of the seller participant.
 * @param createdAt           the timestamp when the conversation was created (ISO‑8601 format).
 * @param lastMessagePreview  a preview (first few characters) of the last message, or null if none.
 * @param lastMessageAt       the timestamp of the last message, or null if none (ISO‑8601 format).
 * @param buyerAdmin          flag indicating whether the buyer has administrator privileges.
 * @param sellerAdmin         flag indicating whether the seller has administrator privileges.
 * @param unreadCount         the number of unread messages for the current user in this conversation.
 */
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
        String lastMessageAt,
        boolean buyerAdmin,
        boolean sellerAdmin,
        int unreadCount
) {
}