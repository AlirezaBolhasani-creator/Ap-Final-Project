package divar.aut.backend.controller;

import divar.aut.backend.dto.AuthResponse;
import divar.aut.backend.dto.LoginRequest;
import divar.aut.backend.dto.RegisterRequest;
import divar.aut.backend.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 * <p>
 * Exposes endpoints for user registration and login. All endpoints are
 * prefixed with {@code /api/auth}. Responses are encapsulated in
 * {@link AuthResponse} objects.
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * The authentication service that handles business logic for
     * registration and login.
     */
    @Autowired
    private AuthService authService;

    /**
     * Registers a new user.
     * <p>
     * Accepts a {@link RegisterRequest} containing user credentials and
     * personal information. The request is validated using JSR-303
     * validation. On success, returns an authentication token and the
     * default role (USER).
     * </p>
     *
     * @param request the registration data, must be valid.
     * @return a {@link ResponseEntity} containing an {@link AuthResponse}
     *         with a success message, the generated JWT token, and the
     *         user role. HTTP status is {@code 200 OK}.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        String token = authService.register(request);
        return ResponseEntity.ok(new AuthResponse("Registration successful", token, "USER"));
    }

    /**
     * Authenticates a user and issues a JWT token.
     * <p>
     * Accepts a {@link LoginRequest} with username/email and password.
     * Validates the credentials against the database. On successful
     * authentication, returns a token along with the user's role.
     * </p>
     *
     * @param loginData the login credentials, must be valid.
     * @return a {@link ResponseEntity} containing an {@link AuthResponse}
     *         with a success message, the JWT token, and the user's role.
     *         HTTP status is {@code 200 OK}.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginData) {
        AuthService.LoginResult result = authService.login(loginData);
        return ResponseEntity.ok(new AuthResponse("Login successful", result.token(), result.role()));
    }
}