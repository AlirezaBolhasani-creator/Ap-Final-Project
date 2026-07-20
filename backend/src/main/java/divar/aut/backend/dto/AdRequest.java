package divar.aut.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating or updating an advertisement.
 * <p>
 * Contains all necessary fields for an ad submission, including title,
 * description, price, condition, and references to a category and city.
 * Validation annotations enforce business rules.
 * </p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdRequest {

    /**
     * The title of the advertisement. Must not be blank and at most 200 characters.
     */
    @NotBlank(message = "must not be blank")
    @Size(max = 200, message = "must be at most 200 characters")
    private String title;

    /**
     * The detailed description of the advertisement. Must not be blank and at most 2000 characters.
     */
    @NotBlank(message = "must not be blank")
    @Size(max = 2000, message = "must be at most 2000 characters")
    private String description;

    /**
     * The price of the item. Must not be null.
     */
    @NotNull(message = "must not be null")
    private double price;

    /**
     * The condition of the item (e.g., "new", "used"). Must not be blank.
     */
    @NotBlank(message = "must not be blank")
    private String itemCondition;

    /**
     * The ID of the category this ad belongs to. Must not be null.
     */
    @NotNull(message = "must not be null")
    private Long categoryId;

    /**
     * The ID of the city where the item is located. Must not be null.
     */
    @NotNull(message = "must not be null")
    private Long cityId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getItemCondition() {
        return itemCondition;
    }

    public void setItemCondition(String itemCondition) {
        this.itemCondition = itemCondition;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }
}