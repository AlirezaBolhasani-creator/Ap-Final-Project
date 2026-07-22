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
    @NotBlank(message = "نباید خالی باشد")
    @Size(max = 100, message = "باید حداکثر ۱۰۰ کاراکتر باشد")
    private String name;

    public String getName() { return name; }
}