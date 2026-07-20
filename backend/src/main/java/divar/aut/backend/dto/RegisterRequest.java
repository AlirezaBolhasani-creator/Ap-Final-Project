package divar.aut.backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for user registration requests.
 * <p>
 * Contains all required fields for creating a new user account:
 * full name, username, password, email (optional), and phone (optional).
 * Validation constraints enforce business rules.
 * </p>
 */
public class RegisterRequest {

    /**
     * The full name of the user. Must not be blank, at most 200 characters.
     * Mapped from JSON field "name" via {@link JsonAlias}.
     */
    @NotBlank(message = "must not be blank")
    @Size(max = 200, message = "must be at most 200 characters")
    @JsonAlias("name")
    private String fullName;

    /**
     * The username for login. Must not be blank, at most 100 characters.
     */
    @NotBlank(message = "must not be blank")
    @Size(max = 100, message = "must be at most 100 characters")
    private String username;

    /**
     * The password for the account. Must not be blank, between 6 and 200 characters.
     */
    @NotBlank(message = "must not be blank")
    @Size(min = 6, max = 200, message = "must be between 6 and 200 characters")
    private String password;

    /**
     * The email address of the user. Optional, but if provided must be at most 200 characters.
     */
    @Size(max = 200, message = "must be at most 200 characters")
    private String email;

    /**
     * The phone number of the user. Optional, but if provided must be at most 50 characters.
     */
    @Size(max = 50, message = "must be at most 50 characters")
    private String phone;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}