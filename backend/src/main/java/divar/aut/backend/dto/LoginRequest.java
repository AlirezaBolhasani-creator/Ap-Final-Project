package divar.aut.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(message = "must not be blank")
    @Size(max = 100, message = "must be at most 100 characters")
    private String username;

    @NotBlank(message = "must not be blank")
    @Size(min = 1, max = 200, message = "must be between 1 and 200 characters")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
