package divar.aut.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MessageRequest {
    @NotBlank(message = "must not be blank")
    @Size(max = 2000, message = "must be at most 2000 characters")
    private String content;

    public String getContent() {
        return content;
    }
}
