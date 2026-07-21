package divar.aut.frontend.model;

import java.util.List;

/**
 * Data Transfer Object representing detailed information about an advertisement.
 * <p>
 * Contains all fields required for displaying the full ad details view, including
 * the ad's metadata, seller profile, images, and aggregated ratings.
 * </p>
 *
 * @param id               the unique identifier of the ad.
 * @param title            the title of the ad.
 * @param description      the detailed description of the item.
 * @param price            the price of the item.
 * @param itemCondition    the condition of the item (e.g., "NEW", "USED").
 * @param status           the current status of the ad (e.g., "PENDING_REVIEW", "ACTIVE").
 * @param categoryName     the name of the category this ad belongs to.
 * @param cityName         the name of the city where the item is located.
 * @param rejectionReason  the reason for rejection if the ad is rejected; may be null.
 * @param seller           the seller's profile information as a {@link SellerProfile}.
 * @param imageFileNames   the list of image file names associated with the ad.
 * @param averageRating    the average rating score given to the seller (0.0 if none).
 * @param ratingCount      the total number of ratings for this seller.
 * @param ratings          the list of detailed ratings for this ad/seller.
 */
public record AdDetailData(
        Long id,
        String title,
        String description,
        double price,
        String itemCondition,
        String status,
        String categoryName,
        String cityName,
        String rejectionReason,
        SellerProfile seller,
        List<String> imageFileNames,
        double averageRating,
        int ratingCount,
        List<RatingData> ratings
) {
    public record SellerProfile(Long id, String username, String fullname) {
    }
}