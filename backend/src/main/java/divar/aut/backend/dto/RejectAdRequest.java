package divar.aut.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectAdRequest(
        @NotBlank(message = "دلیل رد نباید خالی باشد")
        String reason
) {}