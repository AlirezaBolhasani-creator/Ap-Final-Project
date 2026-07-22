package divar.aut.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for sending a new message.
 * <p>
 * Contains only the message content with validation constraints
 * to ensure non‑blank content and a maximum length.
 * </p>
 */
public class MessageRequest {
    @NotBlank(message = "نباید خالی باشد")
    @Size(max = 2000, message = "باید حداکثر ۲۰۰۰ کاراکتر باشد")
    private String content;

    public String getContent() {
        return content;
    }
}