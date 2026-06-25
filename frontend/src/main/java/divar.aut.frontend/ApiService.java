package divar.aut.frontend;

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
                            Matcher matcher = Pattern.compile("\"message\"\\s*:\\s*\"([^\"]+)\"").matcher(responseBody);
                            if (matcher.find()) {
                                String cleanMessage = unescapeJavaString(matcher.group(1));

                                if (response.statusCode() == 200) {
                                    onSuccess.accept(cleanMessage);
                                } else {
                                    onError.accept(cleanMessage);
                                }
                            } else {
                                onError.accept("Unexpected Error");
                            }
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
    private static String unescapeJavaString(String st) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < st.length(); i++) {
            char ch = st.charAt(i);
            if (ch == '\\' && i < st.length() - 1 && st.charAt(i + 1) == 'u') {
                String hex = st.substring(i + 2, i + 6);
                sb.append((char) Integer.parseInt(hex, 16));
                i += 5;
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}
