package divar.aut.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for submitting a rating.
 * <p>
 * Contains a score (1-5) and an optional comment. Used when a user
 * rates a seller after a transaction.
 * </p>
 */
public class RatingRequest {

    /**
     * The rating score, must be between 1 and 5 inclusive.
     * This field is required.
     */
    @NotNull(message = "is required")
    @Min(value = 1, message = "must be between 1 and 5")
    @Max(value = 5, message = "must be between 1 and 5")
    private Integer score;

    /**
     * An optional comment for the rating. If provided, must be at most 1000 characters.
     */
    @Size(max = 1000, message = "must be at most 1000 characters")
    private String comment;

    public Integer getScore() { return score; }
    public String getComment() { return comment; }
}