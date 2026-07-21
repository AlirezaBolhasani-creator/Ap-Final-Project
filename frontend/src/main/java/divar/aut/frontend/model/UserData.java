package divar.aut.frontend.model;

/**
 * Data Transfer Object representing user information.
 * <p>
 * Contains user profile details including authentication, contact information,
 * role, and blocked status. Used for displaying user lists in the admin
 * dashboard and for managing user accounts.
 * </p>
 *
 * @param id       the unique identifier of the user.
 * @param username the user's login username.
 * @param fullname the user's full name.
 * @param email    the user's email address (may be null or empty).
 * @param phone    the user's phone number (may be null or empty).
 * @param role     the user's role (e.g., "USER", "ADMIN").
 * @param blocked  flag indicating whether the user is currently blocked.
 */
public record UserData(Long id, String username, String fullname, String email, String phone,
                       String role, boolean blocked) {
}
