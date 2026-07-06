package divar.aut.frontend.model;

/**
 * Represents the payload sent to the backend when creating or updating an ad.
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
