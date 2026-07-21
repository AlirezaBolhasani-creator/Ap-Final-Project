package divar.aut.frontend.net;

import com.google.gson.Gson;
import divar.aut.frontend.SessionManager;

import java.net.http.HttpResponse;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Service class for authentication operations.
 * <p>
 * Provides methods for user login and registration. Communicates with the backend
 * REST API via {@link ApiClient}. On successful authentication, stores the token
 * and role in {@link SessionManager}.
 * </p>
 */
public class AuthService {

    /**
     * JSON serializer/deserializer for request/response bodies.
     */
    private static final Gson GSON = new Gson();

    /**
     * Sends an authentication request (login) to the server.
     *
     * @param username  the user's username.
     * @param password  the user's password.
     * @param endpoint  the API endpoint (e.g., "/login").
     * @param onSuccess callback receiving the token and role on success.
     * @param onError   callback receiving an error message on failure.
     */
    public static void sendAuthRequest(String username, String password, String endpoint,
                                       BiConsumer<String, String> onSuccess, Consumer<String> onError) {
        if (username.isEmpty() || password.isEmpty()) {
            onError.accept("Please fill all the fields");
            return;
        }
        String jsonBody = GSON.toJson(new LoginRequestBody(username, password));
        ApiClient.send("POST", "/api/auth" + endpoint, jsonBody,
                response -> handleResponse(response, onSuccess, onError), onError);
    }

    /**
     * Sends a registration request to the server.
     *
     * @param name      the user's full name.
     * @param username  the desired username.
     * @param password  the desired password.
     * @param email     the user's email (may be empty).
     * @param phone     the user's phone number (may be empty).
     * @param onSuccess callback receiving the token and role on success.
     * @param onError   callback receiving an error message on failure.
     */
    public static void sendRegisterRequest(String name, String username, String password, String email, String phone,
                                           BiConsumer<String, String> onSuccess, Consumer<String> onError) {
        String jsonBody = GSON.toJson(new RegisterRequestBody(name, username, password, email, phone));
        ApiClient.send("POST", "/api/auth/register", jsonBody,
                response -> handleResponse(response, onSuccess, onError), onError);
    }

    /**
     * Handles the HTTP response from authentication endpoints.
     * Parses the JSON response, extracts token and role, and stores them
     * in the {@link SessionManager} on success.
     *
     * @param response  the HTTP response.
     * @param onSuccess callback accepting the token and role.
     * @param onError   callback accepting an error message.
     */
    private static void handleResponse(HttpResponse<String> response,
                                       BiConsumer<String, String> onSuccess, Consumer<String> onError) {
        AuthApiResponse parsed;
        try {
            parsed = GSON.fromJson(response.body(), AuthApiResponse.class);
        } catch (Exception e) {
            onError.accept("Could not understand the server's response");
            return;
        }

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            String role = (parsed != null && parsed.role != null) ? parsed.role : "USER";
            if (parsed != null && parsed.token != null) {
                SessionManager.getInstance().setToken(parsed.token);
                SessionManager.getInstance().setRole(role);
                onSuccess.accept(parsed.token, role);
            } else {
                String message = (parsed != null && parsed.message != null) ? parsed.message : "Success";
                onSuccess.accept(message, role);
            }
        } else {
            String message = (parsed != null && parsed.message != null) ? parsed.message : "Error occurred";
            onError.accept(message);
        }
    }

    /**
     * Internal record for login request body.
     *
     * @param username the username.
     * @param password the password.
     */
    private record LoginRequestBody(String username, String password) {
    }

    /**
     * Internal record for registration request body.
     * <p>
     * NOTE: The backend's User entity field is "fullname", but AuthController#register
     * never reads or uses the name at all, so this key name has no real effect.
     * Kept as "name" to match the existing request shape.
     * </p>
     *
     * @param name     the user's full name.
     * @param username the desired username.
     * @param password the desired password.
     * @param email    the user's email.
     * @param phone    the user's phone number.
     */
    private record RegisterRequestBody(String name, String username, String password, String email, String phone) {
    }

    /**
     * Internal class representing the shape of the authentication response.
     * Always contains a message; optionally contains a token and/or role.
     */
    private static class AuthApiResponse {
        String message;
        String token;
        String role;
    }
}