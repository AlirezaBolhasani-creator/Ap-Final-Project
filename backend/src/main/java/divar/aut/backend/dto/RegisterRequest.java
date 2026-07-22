package divar.aut.backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for user registration requests.
 * <p>
 * Contains all required fields for creating a new user account:
 * full name, username, password, email, and phone.
 * Validation constraints enforce business rules.
 * </p>
 */
public class RegisterRequest {

    /**
     * The full name of the user. Must not be blank, at most 200 characters,
     * and must contain only letters and spaces with at least a first and
     * last name (two words). Mapped from JSON field "name" via {@link JsonAlias}.
     */
    @NotBlank(message = "نباید خالی باشد")
    @Size(max = 200, message = "باید حداکثر ۲۰۰ کاراکتر باشد")
    @Pattern(
            regexp = "^[\\p{L}]+(\\s[\\p{L}]+)+$",
            message = "باید فقط شامل حروف باشد و نام و نام خانوادگی را با فاصله وارد کنید"
    )
    @JsonAlias("name")
    private String fullName;

    /**
     * The username for login. Must not be blank, at most 100 characters.
     */
    @NotBlank(message = "نباید خالی باشد")
    @Size(max = 100, message = "باید حداکثر ۱۰۰ کاراکتر باشد")
    private String username;

    /**
     * The password for the account. Must not be blank, between 6 and 200 characters.
     */
    @NotBlank(message = "نباید خالی باشد")
    @Size(min = 6, max = 200, message = "باید بین ۶ تا ۲۰۰ کاراکتر باشد")
    private String password;

    /**
     * The email address of the user. Required, must be at most
     * 200 characters and a well-formed email address.
     */
    @NotBlank(message = "نباید خالی باشد")
    @Size(max = 200, message = "باید حداکثر ۲۰۰ کاراکتر باشد")
    @Pattern(
            regexp = "^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$",
            message = "باید یک ایمیل معتبر باشد"
    )
    private String email;

    /**
     * The phone number of the user. Required, must be a valid
     * Iranian mobile number (11 digits starting with 09).
     */
    @NotBlank(message = "نباید خالی باشد")
    @Size(max = 50, message = "باید حداکثر ۵۰ کاراکتر باشد")
    @Pattern(
            regexp = "^09\\d{9}$",
            message = "باید یک شماره موبایل معتبر باشد (مثلاً: 09xxxxxxxxx)"
    )
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
