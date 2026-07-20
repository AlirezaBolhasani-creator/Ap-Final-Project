package divar.aut.backend.dto;

import divar.aut.backend.entity.Category;

/**
 * Data Transfer Object for category responses.
 * <p>
 * Contains the ID and name of a category, built from a {@link Category} entity.
 * </p>
 */
public class CategoryResponse {
    /**
     * The unique identifier of the category.
     */
    private Long id;

    /**
     * The name of the category.
     */
    private String name;

    private Long parentId;
    /**
     * Constructs a CategoryResponse from a Category entity.
     *
     * @param category the category entity.
     */
    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.parentId = category.getParent() != null? category.getParent().getId() : null;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}