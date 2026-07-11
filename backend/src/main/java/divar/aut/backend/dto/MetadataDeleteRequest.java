package divar.aut.backend.dto;

import jakarta.validation.constraints.NotNull;

public class MetadataDeleteRequest {

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
