package divar.aut.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating or updating a category.
 * <p>
 * Contains only the category name with validation constraints.
 * </p>
 */
public class CategoryRequest {
    @NotBlank(message = "must not be blank")
    @Size(max = 100, message = "must be at most 100 characters")
    private String name;

    private Long parentId;

    public Long getParentId() {return parentId;}

    public String getName() {return name; }
}