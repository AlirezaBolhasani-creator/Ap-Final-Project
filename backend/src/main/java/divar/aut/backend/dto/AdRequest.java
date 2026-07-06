package divar.aut.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AdRequest {

    @NotBlank(message = "must not be blank")
    @Size(max = 200, message = "must be at most 200 characters")
    private String title;

    @NotBlank(message = "must not be blank")
    @Size(max = 2000, message = "must be at most 2000 characters")
    private String description;

    @NotNull(message = "must not be null")
    private double price;

    @NotBlank(message = "must not be blank")
    private String itemCondition;

    @NotNull(message = "must not be null")
    private Long categoryId;

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
