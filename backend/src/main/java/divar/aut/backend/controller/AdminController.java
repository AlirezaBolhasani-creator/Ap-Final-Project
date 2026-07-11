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

    @GetMapping("/ads")
    public List<AdSummaryResponse> listAllAds() { return adminService.listAllAds(); }

    @DeleteMapping("/ads/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable Long id) {
        adminService.deleteAd(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public List<UserResponse> listUsers() { return adminService.listUsers(); }

    @PutMapping("/users/{id}/block")
    public ResponseEntity<Void> blockUser(@PathVariable Long id) {
        adminService.setBlocked(id, true);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/unblock")
    public ResponseEntity<Void> unblockUser(@PathVariable Long id) {
        adminService.setBlocked(id, false);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    @PutMapping("/categories/{id}")
    public CategoryResponse updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(id, request);
    }

    @GetMapping("/categories/{id}/usage")
    public MetadataUsageResponse getCategoryUsage(@PathVariable Long id) {
        return new MetadataUsageResponse(metadataDeletionService.countCategoryAds(id));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id,
                                               @Valid @RequestBody MetadataDeleteRequest request) {
        metadataDeletionService.deleteCategory(id, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cities")
    public ResponseEntity<CityResponse> createCity(@Valid @RequestBody CityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cityService.create(request));
    }

    @PutMapping("/cities/{id}")
    public CityResponse updateCity(@PathVariable Long id, @Valid @RequestBody CityRequest request) {
        return cityService.update(id, request);
    }

    @GetMapping("/cities/{id}/usage")
    public MetadataUsageResponse getCityUsage(@PathVariable Long id) {
        return new MetadataUsageResponse(metadataDeletionService.countCityAds(id));
    }

    @DeleteMapping("/cities/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id,
                                           @Valid @RequestBody MetadataDeleteRequest request) {
        metadataDeletionService.deleteCity(id, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public AdminStatsResponse getStats() { return adminService.getStats(); }
}
