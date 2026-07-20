package divar.aut.backend.controller;

import divar.aut.backend.dto.CityResponse;
import divar.aut.backend.service.CityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for city-related operations.
 * <p>
 * Provides an endpoint to retrieve all available cities.
 * All endpoints are prefixed with {@code /api/cities}.
 * </p>
 */
@RestController
@RequestMapping("/api/cities")
public class CityController {

    /**
     * The service layer for city business logic.
     */
    private final CityService cityService;

    /**
     * Constructs a new CityController with the required service.
     *
     * @param cityService the city service to be used.
     */
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    /**
     * Retrieves a list of all cities.
     * <p>
     * Returns a collection of {@link CityResponse} objects representing
     * each available city, typically sorted by a default order (e.g., by name).
     * </p>
     *
     * @return a list of {@link CityResponse} objects.
     */
    @GetMapping
    public List<CityResponse> listAll() {
        return cityService.listAll();
    }
}