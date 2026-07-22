package divar.aut.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * A registered account. Despite the getter/setter names (kept as
 * "password" to avoid an unrelated DB column rename), the value stored here
 * is always a BCrypt hash after AuthService#register runs - the raw
 * password is never persisted.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "نباید خالی باشد")
    @Size(max = 100, message = "باید حداکثر ۱۰۰ کاراکتر باشد")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "نباید خالی باشد")
    @Size(min = 6, max = 200, message = "باید بین ۶ تا ۲۰۰ کاراکتر باشد")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "نباید خالی باشد")
    @Size(max = 200, message = "باید حداکثر ۲۰۰ کاراکتر باشد")
    private String fullname;

    @NotBlank(message = "نباید خالی باشد")
    @Size(max = 200, message = "باید حداکثر ۲۰۰ کاراکتر باشد")
    private String email;

    @NotBlank(message = "نباید خالی باشد")
    @Size(max = 50, message = "باید حداکثر ۵۰ کاراکتر باشد")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    // A blocked user can no longer log in (see AuthService); enforced again
    // on every request in JwtAuthenticationFilter in case they were blocked
    // after their token was already issued.
    @Column(nullable = false)
    private boolean blocked = false;

    public User() {
    }

    public User(String username, String password, String fullname, String email, String phone) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
