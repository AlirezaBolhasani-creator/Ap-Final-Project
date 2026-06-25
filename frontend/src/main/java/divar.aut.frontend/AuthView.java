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

public class AuthView extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("به سامانه خرید و فروش دست دوم خوش آمدید! 🚀");

        TextField username = new TextField();
        username.setPromptText("username");

        PasswordField password = new PasswordField();
        password.setPromptText("password");

        Button register = new Button("register");
        Button login = new Button("login");

        Label statusLabel = new Label("");

        login.setOnAction(event -> {
            ApiService.sendAuthRequest(
                    username.getText(),
                    password.getText(),
                    "/login",
                    successMsg -> setStatus(statusLabel, successMsg, true),
                    errorMsg -> setStatus(statusLabel, errorMsg, false)
            );
        });

        register.setOnAction(event -> {
            ApiService.sendAuthRequest(
                    username.getText(),
                    password.getText(),
                    "/register",
                    successMsg -> setStatus(statusLabel, successMsg, true),
                    errorMsg -> setStatus(statusLabel, errorMsg, false)
            );
        });

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(label, username, password, login, register, statusLabel);

        Scene scene = new Scene(root, 350, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setStatus(Label label, String message, boolean isSuccess) {
        label.setText(message);
        if (isSuccess) {
            label.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            label.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}