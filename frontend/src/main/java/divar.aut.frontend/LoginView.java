package divar.aut.frontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class LoginView {
    public Scene getScene() {
        Label label = new Label("بخش ورود به حساب کاربری!");
        TextField username = new TextField();
        username.setPromptText("username");
        PasswordField password = new PasswordField();
        password.setPromptText("password");
        Button login = new Button("Login");
        Label statusLabel = new Label("");

        Button back = new Button("Back");
        login.setOnAction(event -> {
            ApiService.sendAuthRequest(username.getText(), password.getText(), "/login",
                    success -> setStatus(statusLabel, success, true),
                    error -> setStatus(statusLabel, error, false));
        });
        back.setOnAction(event -> {
            DivarApplication.showWelcome();
        });

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(label, username, password, login, back, statusLabel);

        return new Scene(root, 350, 300);
    }

    private void setStatus(Label label, String message, boolean isSuccess) {
        label.setText(message);
        label.setStyle("-fx-text-fill: " + (isSuccess ? "green" : "red") + "; -fx-font-weight: bold;");
    }
}