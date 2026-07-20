package divar.aut.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating or updating a city.
 * <p>
 * Contains only the city name with validation constraints.
 * </p>
 */
public class CityRequest {
    @NotBlank(message = "must not be blank")
    @Size(max = 100, message = "must be at most 100 characters")
    private String name;

    public String getName() { return name; }
}