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
    @NotBlank(message = "must not be blank")
    @Size(max = 2000, message = "must be at most 2000 characters")
    private String content;

    public String getContent() {
        return content;
    }
}