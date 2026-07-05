package divar.aut.frontend;

/**
 * Holds the currently logged-in user's token/role for as long as the app is
 * running. A desktop client only ever has one active session, so a
 * singleton is used here instead of threading this state through every
 * screen and service constructor by hand.
 */
public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    private String token;
    private String role;

    private SessionManager() {
        // use getInstance()
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    /** Logout: the frontend simply forgets the token, nothing to tell the server. */
    public void endSession() {
        this.token = null;
        this.role = null;
    }

    public boolean isLoggedIn() {
        return token != null;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
