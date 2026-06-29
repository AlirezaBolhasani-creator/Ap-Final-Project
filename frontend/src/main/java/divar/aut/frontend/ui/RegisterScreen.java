package divar.aut.frontend.ui;

import divar.aut.frontend.ApiService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class RegisterScreen {
    private VBox view;
    private ViewManager mainApp;
    private Label statusLabel;
    public RegisterScreen(ViewManager mainApp) {
        this.mainApp = mainApp;
        buildUI();
    }

    private void buildUI() {
        view = new VBox(15);
        view.setMaxSize(400, 500);
        view.setPadding(new Insets(30));
        view.setAlignment(Pos.CENTER);

        statusLabel = new Label("");
        statusLabel.setTextFill(Color.YELLOW);
        view.setStyle("-fx-background-color: rgba(255, 255, 255, 0.15); " +
                "-fx-background-radius: 20; " +
                "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                "-fx-border-radius: 20; " +
                "-fx-border-width: 1;");

        Label label = new Label("ثبت نام در سامانه");
        label.setTextFill(Color.WHITE);

        String fieldStyle = "-fx-background-color: transparent; -fx-border-color: white; -fx-border-width: 0 0 1 0; -fx-text-fill: white; -fx-prompt-text-fill: #cccccc;";

        TextField name = createField("Name", fieldStyle);
        TextField username = createField("Username", fieldStyle);
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        password.setStyle(fieldStyle);
        TextField email = createField("Email", fieldStyle);
        TextField phone = createField("Phone Number", fieldStyle);

        Button register = new Button("Register");
        register.setPrefWidth(Double.MAX_VALUE);
        register.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-font-weight: bold;");

        register.setOnAction(event -> {
            ApiService.sendRegisterRequest(
                    name.getText(),
                    username.getText(),
                    password.getText(),
                    email.getText(),
                    phone.getText(),
                    message -> {
                        statusLabel.setText("Success: " + message);
                        statusLabel.setTextFill(Color.GREEN);
                        mainApp.toMain();
                        },
                    error -> {
                        statusLabel.setText("Error: " + error);
                        statusLabel.setTextFill(Color.RED);
                    }
            );
        });

        Button back = new Button("Back");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-underline: true;");
        back.setOnAction(event -> mainApp.toWelcome());

        view.getChildren().addAll(label, name, username, password, email, phone, register, back, statusLabel);
    }

    private TextField createField(String prompt, String style) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle(style);
        return field;
    }

    public VBox getView() {
        return view;
    }
}