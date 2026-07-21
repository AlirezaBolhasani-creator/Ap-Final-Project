package divar.aut.frontend.model;

/**
 * Immutable data holder for a single advertisement summary.
 * <p>
 * Contains the core fields displayed in ad cards and lists, such as title,
 * price, condition, status, location, category, and a thumbnail image.
 * This is a lightweight representation used for browsing, as opposed to
 * the more detailed {@link AdDetailData}.
 * </p>
 *
 * @param id                the unique identifier of the ad.
 * @param title             the title of the advertisement.
 * @param price             the price of the item; may be null if not set.
 * @param itemCondition     the condition of the item (e.g., "NEW", "USED").
 * @param status            the current status of the ad (e.g., "ACTIVE", "PENDING_REVIEW").
 * @param cityName          the name of the city where the item is located.
 * @param categoryName      the name of the category this ad belongs to.
 * @param thumbnailFileName the filename of the thumbnail image for display.
 */
public record AdData(
        Long id,
        String title,
        Double price,
        String itemCondition,
        String status,
        String cityName,
        String categoryName,
        String thumbnailFileName
) {
}