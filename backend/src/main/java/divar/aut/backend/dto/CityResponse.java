package divar.aut.backend.dto;

import divar.aut.backend.entity.City;

/**
 * Data Transfer Object for city responses.
 * <p>
 * Contains the ID and name of a city, built from a {@link City} entity.
 * </p>
 */
public class CityResponse {

    /**
     * The unique identifier of the city.
     */
    private Long id;

    /**
     * The name of the city.
     */
    private String name;

    /**
     * Constructs a CityResponse from a City entity.
     *
     * @param city the city entity.
     */
    public CityResponse(City city) {
        this.id = city.getId();
        this.name = city.getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
