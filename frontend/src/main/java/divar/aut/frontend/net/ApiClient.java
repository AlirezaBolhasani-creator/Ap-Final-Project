package divar.aut.frontend.net;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import divar.aut.frontend.SessionManager;
import divar.aut.frontend.config.ApiConfig;
import javafx.application.Platform;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.function.Consumer;

/**
 * Shared HTTP plumbing for every *Service class (AuthService, AdService,
 * ...). Centralizes the single HttpClient instance, base-URL/Authorization
 * header handling, and async-to-UI-thread dispatching, so individual
 * services only need to know their own endpoint paths and JSON shapes
 * instead of each building their own HttpClient and repeating this logic.
 */
public final class ApiClient {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();

    private ApiClient() {
        // static helpers only
    }

    /**
     * Sends a request with an optional JSON string body. The response (or
     * error) is always delivered on the JavaFX application thread.
     */
    public static void send(String method, String path, String jsonBodyOrNull,
                             Consumer<HttpResponse<String>> onSuccess, Consumer<String> onError) {
        HttpRequest.Builder builder = requestBuilder(path).header("Content-Type", "application/json");
        builder.method(method, jsonBodyOrNull == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(jsonBodyOrNull));

        HTTP_CLIENT.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> onSuccess.accept(response)))
                .exceptionally(ex -> {
                    Platform.runLater(() -> onError.accept("Could not reach the server: " + ex.getMessage()));
                    return null;
                });
    }

    /** Uploads a single file as multipart/form-data under the given form field name. */
    public static void uploadFile(String path, String fieldName, File file,
                                   Consumer<HttpResponse<String>> onSuccess, Consumer<String> onError) {
        try {
            String boundary = "SecondhandBoundary" + System.currentTimeMillis();
            ByteArrayOutputStream bodyBytes = new ByteArrayOutputStream();
            bodyBytes.write(("--" + boundary + "\r\n").getBytes());
                String disposition = "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + file.getName() + "\"\r\n";
                bodyBytes.write(disposition.getBytes());
            bodyBytes.write("Content-Type: application/octet-stream\r\n\r\n".getBytes());
            bodyBytes.write(Files.readAllBytes(file.toPath()));
            bodyBytes.write(("\r\n--" + boundary + "--\r\n").getBytes());

            HttpRequest request = requestBuilder(path)
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(bodyBytes.toByteArray()))
                    .build();

            HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> Platform.runLater(() -> onSuccess.accept(response)))
                    .exceptionally(ex -> {
                        Platform.runLater(() -> onError.accept("Could not reach the server: " + ex.getMessage()));
                        return null;
                    });
        } catch (IOException e) {
            onError.accept("Could not read file: " + e.getMessage());
        }
    }

    /** Uploads multiple files as multipart/form-data under the same form field name. */
    public static void uploadFiles(String path, String fieldName, java.util.List<File> files,
                                   Consumer<HttpResponse<String>> onSuccess, Consumer<String> onError) {
        if (files == null || files.isEmpty()) {
            onError.accept("No files selected");
            return;
        }
        try {
            String boundary = "SecondhandBoundary" + System.currentTimeMillis();
            ByteArrayOutputStream bodyBytes = new ByteArrayOutputStream();
            for (File file : files) {
                bodyBytes.write(("--" + boundary + "\r\n").getBytes());
                String disposition = "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + file.getName() + "\"\r\n";
                bodyBytes.write(disposition.getBytes());
                bodyBytes.write("Content-Type: application/octet-stream\r\n\r\n".getBytes());
                bodyBytes.write(Files.readAllBytes(file.toPath()));
                bodyBytes.write("\r\n".getBytes());
            }
            bodyBytes.write(("--" + boundary + "--\r\n").getBytes());

            HttpRequest request = requestBuilder(path)
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(bodyBytes.toByteArray()))
                    .build();

            HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> Platform.runLater(() -> onSuccess.accept(response)))
                    .exceptionally(ex -> {
                        Platform.runLater(() -> onError.accept("Could not reach the server: " + ex.getMessage()));
                        return null;
                    });
        } catch (IOException e) {
            onError.accept("Could not read file: " + e.getMessage());
        }
    }

    /**
     * Reads backend's structured error shape ({"message":...,"status":...})
     * and falls back to a generic text when parsing is not possible.
     */
    public static String extractErrorMessage(HttpResponse<String> response, String fallbackPrefix) {
        try {
            JsonObject json = GSON.fromJson(response.body(), JsonObject.class);
            if (json != null && json.has("message") && json.get("message").isJsonPrimitive()) {
                return json.get("message").getAsString();
            }
        } catch (Exception ignored) {
            // fall back below
        }
        return fallbackPrefix + response.statusCode();
    }

    private static HttpRequest.Builder requestBuilder(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(ApiConfig.SERVER_URL + path));
        String token = SessionManager.getInstance().getToken();
        if (token != null && !token.isBlank() && !"null".equalsIgnoreCase(token)) {
            builder.header("Authorization", "Bearer " + token);
        }
        return builder;
    }
}
