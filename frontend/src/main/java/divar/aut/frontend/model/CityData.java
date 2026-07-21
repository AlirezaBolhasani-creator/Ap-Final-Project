package divar.aut.frontend.model;

/**
 * Data Transfer Object representing a city.
 * <p>
 * Contains the unique identifier and name of a city used for localizing
 * advertisements.
 * </p>
 *
 * @param id   the unique identifier of the city.
 * @param name the name of the city.
 */
public record CityData(Long id, String name) {
}