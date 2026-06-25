package divar.aut.frontend;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class MainApplication extends Application {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    public void start(Stage primaryStage) {
        Label label = new Label("به سامانه خرید و فروش دست دوم خوش آمدید! 🚀");
        TextField username = new TextField();
        username.setPromptText("username");

        PasswordField password = new PasswordField();
        password.setPromptText("password");

        Button register = new Button("register");
        Button login = new Button("login");

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: blue;");


        login.setOnAction(event -> {sendAuthRequest(username.getText(),password.getText(), "/login", statusLabel);});
        register.setOnAction(event -> {sendAuthRequest(username.getText(),password.getText(), "/register", statusLabel);});

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(label, username, password, login, register, statusLabel);

        Scene scene = new Scene(root, 350, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void sendAuthRequest(String username, String password, String endpoint, Label statusLabel) {
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill all the fields");
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
                    .thenApply(HttpResponse::body)
                    .thenAccept(responseBody -> {
                        javafx.application.Platform.runLater(() -> {
                            java.util.regex.Matcher matcher = java.util.regex.Pattern
                                    .compile("\"message\"\\s*:\\s*\"([^\"]+)\"")
                                    .matcher(responseBody);

                            if (matcher.find()) {
                                String cleanMessage = matcher.group(1);

                                cleanMessage = unescapeJavaString(cleanMessage);

                                statusLabel.setText(cleanMessage);
                            } else {
                                statusLabel.setText("bad message from server");
                            }
                        });
                    })
                    .exceptionally(ex -> {
                        javafx.application.Platform.runLater(() -> statusLabel.setText("خطا در اتصال به سرور!"));
                        return null;
                    });
        } catch (Exception ex) {
            statusLabel.setText("Unexpected Error");
        }
    }

    private String unescapeJavaString(String st) {
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
    }    public static void main(String[] args) {
        launch(args);
    }
}