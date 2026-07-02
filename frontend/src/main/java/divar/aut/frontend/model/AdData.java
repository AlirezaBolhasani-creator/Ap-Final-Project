package divar.aut.frontend.model;

/**
 * Immutable data holder for a single advertisement.
 * Replace with your real model/DTO class.
 */
public record AdData(
        String title,
        Double price,
        String location,
        String condition,
        String imageUrl,
        int photoCount,
        Long id
) {}