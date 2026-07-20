package divar.aut.backend.dto;

/**
 * Data Transfer Object for authentication responses.
 * <p>
 * Contains a message, a JWT token, and the user's role returned
 * after successful registration or login.
 * </p>
 */
public class AuthResponse {

    /**
     * A human-readable message describing the outcome (e.g., "Login successful").
     */
    private String message;

    /**
     * The JWT authentication token for the authenticated user.
     */
    private String token;

    /**
     * The role of the authenticated user (e.g., "USER", "ADMIN").
     */
    private String role;

    /**
     * Constructs a new AuthResponse with the given values.
     *
     * @param message the response message.
     * @param token   the JWT token.
     * @param role    the user role.
     */
    public AuthResponse(String message, String token, String role) {
        this.message = message;
        this.token = token;
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }
}