package divar.aut.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for login requests.
 * <p>
 * Contains the user's credentials (username and password) with validation
 * constraints to ensure non‑blank values and appropriate length limits.
 * </p>
 */
public class LoginRequest {

    /**
     * The username of the user attempting to log in.
     * Must not be blank and at most 100 characters.
     */
    @NotBlank(message = "must not be blank")
    @Size(max = 100, message = "must be at most 100 characters")
    private String username;

    /**
     * The password of the user attempting to log in.
     * Must not be blank and between 1 and 200 characters.
     */
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