package divar.aut.backend.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for metadata deletion requests.
 * <p>
 * Used when deleting a category or city to specify the deletion strategy
 * and an optional replacement ID. The strategy determines how associated
 * advertisements are handled (e.g., reassign to another metadata entity).
 * </p>
 */
public class MetadataDeleteRequest {

    /**
     * The deletion strategy to apply.
     * Must not be null.
     */
    @NotNull(message = "must not be null")
    private MetadataDeleteStrategy strategy;
    private Long replacementId;

    public MetadataDeleteStrategy getStrategy() {
        return strategy;
    }

    public Long getReplacementId() {
        return replacementId;
    }
}