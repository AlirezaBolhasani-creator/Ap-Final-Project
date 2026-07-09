package divar.aut.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectAdRequest(
        @NotBlank(message = "Rejection reason must not be empty")
        String reason
) {}