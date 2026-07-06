package divar.aut.frontend.model;

/**
 * Immutable data holder for a single advertisement.
 * Replace with your real model/DTO class.
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
