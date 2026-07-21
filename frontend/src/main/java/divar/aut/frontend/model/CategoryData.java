package divar.aut.frontend.model;

/**
 * Data Transfer Object representing a category.
 * <p>
 * Contains the unique identifier and name of a category used to classify
 * advertisements.
 * </p>
 *
 * @param id   the unique identifier of the category.
 * @param name the name of the category.
 */
public record CategoryData(Long id, String name) {
}