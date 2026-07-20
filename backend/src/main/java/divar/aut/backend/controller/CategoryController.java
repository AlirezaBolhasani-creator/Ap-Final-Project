package divar.aut.backend.controller;

import divar.aut.backend.dto.CategoryResponse;
import divar.aut.backend.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for category-related operations.
 * <p>
 * Provides an endpoint to retrieve all available categories.
 * All endpoints are prefixed with {@code /api/categories}.
 * </p>
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    /**
     * The service layer for category business logic.
     */
    private final CategoryService categoryService;

    /**
     * Constructs a new CategoryController with the required service.
     *
     * @param categoryService the category service to be used.
     */
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Retrieves a list of all categories.
     * <p>
     * Returns a collection of {@link CategoryResponse} objects representing
     * </p>
     * @return a list of {@link CategoryResponse} objects.
     */
    @GetMapping
    public List<CategoryResponse> listAll() {
        return categoryService.listAll();
    }
}