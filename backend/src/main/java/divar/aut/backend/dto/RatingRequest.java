package divar.aut.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RatingRequest {

    @NotNull(message = "is required")
    @Min(value = 1, message = "must be between 1 and 5")
    @Max(value = 5, message = "must be between 1 and 5")
    private Integer score;

    @Size(max = 1000, message = "must be at most 1000 characters")
    private String comment;

    public Integer getScore() { return score; }
    public String getComment() { return comment; }
}
