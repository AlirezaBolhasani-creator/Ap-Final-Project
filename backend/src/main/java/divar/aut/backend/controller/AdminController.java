package divar.aut.backend.controller;

import divar.aut.backend.dto.*;
import divar.aut.backend.service.AdminService;
import divar.aut.backend.service.CategoryService;
import divar.aut.backend.service.CityService;
import divar.aut.backend.service.MetadataDeletionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for administrative operations.
 * <p>
 * Exposes endpoints for managing ads, users, categories, cities, and retrieving
 * system statistics. All endpoints are prefixed with {@code /api/admin}.
 * </p>
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final CategoryService categoryService;
    private final CityService cityService;
    private final MetadataDeletionService metadataDeletionService;

    public AdminController(AdminService adminService, CategoryService categoryService, CityService cityService,
                           MetadataDeletionService metadataDeletionService) {
        this.adminService = adminService;
        this.categoryService = categoryService;
        this.cityService = cityService;
        this.metadataDeletionService = metadataDeletionService;
    }

    /**
     * Retrieves a list of all advertisements.
     *
     * @return list of {@link AdSummaryResponse} objects representing all ads.
     */
    @GetMapping("/ads")
    public List<AdSummaryResponse> listAllAds() {
        return adminService.listAllAds();
    }

    /**
     * Deletes an advertisement by its unique identifier.
     * <p>
     * Returns a {@code 204 No Content} response on successful deletion.
     * </p>
     *
     * @param id the ID of the ad to delete.
     * @return an empty response entity with status {@code 204 No Content}.
     */
    @DeleteMapping("/ads/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable Long id) {
        adminService.deleteAd(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a list of all registered users.
     *
     * @return list of {@link UserResponse} objects representing all users.
     */
    @GetMapping("/users")
    public List<UserResponse> listUsers() {
        return adminService.listUsers();
    }

    /**
     * Blocks a user by their unique identifier.
     * <p>
     * The user will be prevented from performing actions. Returns {@code 200 OK}
     * on success.
     * </p>
     *
     * @param id the ID of the user to block.
     * @return an empty response entity with status {@code 200 OK}.
     */
    @PutMapping("/users/{id}/block")
    public ResponseEntity<Void> blockUser(@PathVariable Long id) {
        adminService.setBlocked(id, true);
        return ResponseEntity.ok().build();
    }

    /**
     * Unblocks a user by their unique identifier.
     * <p>
     * Restores the user's ability to perform actions. Returns {@code 200 OK}
     * on success.
     * </p>
     *
     * @param id the ID of the user to unblock.
     * @return an empty response entity with status {@code 200 OK}.
     */
    @PutMapping("/users/{id}/unblock")
    public ResponseEntity<Void> unblockUser(@PathVariable Long id) {
        adminService.setBlocked(id, false);
        return ResponseEntity.ok().build();
    }

    /**
     * Creates a new category.
     * <p>
     * The request body must be valid (validated via {@link Valid}).
     * On successful creation, returns a {@code 201 Created} response with the
     * created category data.
     * </p>
     *
     * @param request the category creation data.
     * @return a response entity containing the created {@link CategoryResponse}
     *         and status {@code 201 Created}.
     */
    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    /**
     * Updates an existing category identified by its ID.
     * <p>
     * The request body must be valid. Returns the updated category data.
     * </p>
     *
     * @param id      the ID of the category to update.
     * @param request the updated category data.
     * @return the updated {@link CategoryResponse}.
     */
    @PutMapping("/categories/{id}")
    public CategoryResponse updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(id, request);
    }

    /**
     * Retrieves the number of advertisements that belong to a specific category.
     *
     * @param id the ID of the category.
     * @return a {@link MetadataUsageResponse} containing the usage count.
     */
    @GetMapping("/categories/{id}/usage")
    public MetadataUsageResponse getCategoryUsage(@PathVariable Long id) {
        return new MetadataUsageResponse(metadataDeletionService.countCategoryAds(id));
    }

    /**
     * Deletes a category by its ID, with a deletion strategy specified in the request.
     * <p>
     * The request must include a valid deletion strategy (e.g., reassign or delete ads).
     * Returns {@code 204 No Content} on successful deletion.
     * </p>
     *
     * @param id      the ID of the category to delete.
     * @param request the deletion request containing the strategy.
     * @return an empty response entity with status {@code 204 No Content}.
     */
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id,
                                               @Valid @RequestBody MetadataDeleteRequest request) {
        metadataDeletionService.deleteCategory(id, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Creates a new city.
     * <p>
     * The request body must be valid. On success, returns {@code 201 Created}
     * with the created city data.
     * </p>
     *
     * @param request the city creation data.
     * @return a response entity containing the created {@link CityResponse}
     *         and status {@code 201 Created}.
     */
    @PostMapping("/cities")
    public ResponseEntity<CityResponse> createCity(@Valid @RequestBody CityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cityService.create(request));
    }

    /**
     * Updates an existing city identified by its ID.
     * <p>
     * The request body must be valid. Returns the updated city data.
     * </p>
     *
     * @param id      the ID of the city to update.
     * @param request the updated city data.
     * @return the updated {@link CityResponse}.
     */
    @PutMapping("/cities/{id}")
    public CityResponse updateCity(@PathVariable Long id, @Valid @RequestBody CityRequest request) {
        return cityService.update(id, request);
    }

    /**
     * Retrieves the number of advertisements that belong to a specific city.
     *
     * @param id the ID of the city.
     * @return a {@link MetadataUsageResponse} containing the usage count.
     */
    @GetMapping("/cities/{id}/usage")
    public MetadataUsageResponse getCityUsage(@PathVariable Long id) {
        return new MetadataUsageResponse(metadataDeletionService.countCityAds(id));
    }

    /**
     * Deletes a city by its ID, with a deletion strategy specified in the request.
     * <p>
     * The request must include a valid deletion strategy. Returns {@code 204 No Content}
     * on successful deletion.
     * </p>
     *
     * @param id      the ID of the city to delete.
     * @param request the deletion request containing the strategy.
     * @return an empty response entity with status {@code 204 No Content}.
     */
    @DeleteMapping("/cities/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id,
                                           @Valid @RequestBody MetadataDeleteRequest request) {
        metadataDeletionService.deleteCity(id, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves system statistics for the admin dashboard.
     * <p>
     * Includes metrics such as total users, ads, categories, cities, etc.
     * </p>
     *
     * @return an {@link AdminStatsResponse} containing the statistics.
     */
    @GetMapping("/stats")
    public AdminStatsResponse getStats() {
        return adminService.getStats();
    }
}