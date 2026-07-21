package divar.aut.frontend.model;

/**
 * Represents the payload sent to the backend when creating or updating an ad.
 *
 * @param categoryId    the ID of the category to which the ad belongs.
 * @param cityId        the ID of the city where the item is located.
 * @param title         the title of the ad (must not be blank).
 * @param description   the detailed description of the item (must not be blank).
 * @param price         the price of the item.
 * @param itemCondition the condition of the item (e.g., "NEW", "USED").
 */
public record AdRequestData(
        Long categoryId,
        Long cityId,
        String title,
        String description,
        double price,
        String itemCondition
) {
}