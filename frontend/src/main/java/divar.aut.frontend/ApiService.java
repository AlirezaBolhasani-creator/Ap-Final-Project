package divar.aut.frontend;

import divar.aut.frontend.authpart.AuthManager;
import javafx.application.Platform;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiService
{
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static void sendAuthRequest(String username, String password, String endpoint,
                                       Consumer<String> onSuccess, Consumer<String> onError)
    {
        if(username.isEmpty() || password.isEmpty())
        {
            onError.accept("Please fill all the fields");
            return;
        }
        try {
            String jsonBody = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/auth" + endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        String responseBody = response.body();
                        Platform.runLater(() -> {
                            handleResponse(response, onSuccess, onError);
                        });
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> onError.accept("Error connecting to server!"));
                        return null;
                    });
        } catch (Exception ex) {
            onError.accept("Unexpected Error");
        }
    }
    public static void sendRegisterRequest(String name, String username, String password, String email, String phone,
                                           Consumer<String> onSuccess, Consumer<String> onError) {
        try {
            String jsonBody = String.format(
                    "{\"name\":\"%s\", \"username\":\"%s\", \"password\":\"%s\", \"email\":\"%s\", \"phone\":\"%s\"}",
                    name, username, password, email, phone
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/auth/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        handleResponse(response, onSuccess, onError);

                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> onError.accept("Error Connecting To Server: " + ex.getMessage()));
                        return null;
                    });

        } catch (Exception ex) {
            onError.accept("Unexpected Error: " + ex.getMessage());
        }
    }
    private static void handleResponse(HttpResponse<String> response,
                                       Consumer<String> onSuccess, Consumer<String> onError) {
        String responseBody = response.body();
        System.out.println("Server Response: " + responseBody);
        Platform.runLater(() -> {
            Matcher messageMatcher = Pattern.compile("\"message\"\\s*:\\s*\"([^\"]+)\"").matcher(responseBody);
            Matcher tokenMatcher = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"").matcher(responseBody);

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                if (tokenMatcher.find()) {
                    AuthManager.setToken(tokenMatcher.group(1));
                }
                onSuccess.accept(messageMatcher.find() ? messageMatcher.group(1) : "Success");
            } else {
                onError.accept(messageMatcher.find() ? messageMatcher.group(1) : "Error occurred");
            }
        });
    }
}
