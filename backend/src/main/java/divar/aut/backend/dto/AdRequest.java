package divar.aut.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AdRequest {

    @NotBlank(message = "must not be blank")
    @Size(max = 200, message = "must be at most 200 characters")
    private String title;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "must be greater than or equal to 0")
    private BigDecimal price;

    @NotBlank(message = "must not be blank")
    @Size(max = 200, message = "must be at most 200 characters")
    private String location;

    @NotBlank(message = "must not be blank")
    private String condition;

    @NotBlank(message = "must not be blank")
    private String category;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
