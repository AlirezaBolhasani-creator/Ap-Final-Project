package divar.aut.frontend.ui;
import divar.aut.frontend.service.ApiService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class LoginScreen
{
    private ViewManager mainApp;
    private VBox view;
    private Label statusLabel;
    public  LoginScreen(ViewManager mainApp)
    {
        this.mainApp = mainApp;
        buildUI();
    }
    public VBox getView()
    {
        return view;
    }
    private void buildUI() {
        view = new VBox(20);
        view.setMaxSize(380, 400);
        view.setPadding(new Insets(40));
        view.setAlignment(Pos.CENTER);
        view.setStyle(WelcomeScreen.getGlassStyle());

        statusLabel = new Label("");
        statusLabel.setTextFill(Color.YELLOW);

        Label title = new Label("LOGIN");
        title.setTextFill(Color.WHITE);

        TextField UserName = new TextField();
        UserName.setPromptText("username");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");

        Button btnLogin = WelcomeScreen.createStyledButton("SIGN IN");

        btnLogin.setOnAction(e -> {
            String username = UserName.getText();
            String password = passField.getText();

            ApiService.sendAuthRequest(username, password, "/login",
                    (token, role) -> {
                        mainApp.setUserToken(token);
                        mainApp.setUserRole(role);
                        statusLabel.setText("Success");
                        statusLabel.setTextFill(Color.GREEN);
                        mainApp.toMain();
                    },
                    error -> {
                        // Error
                        statusLabel.setText("Error: " + error);
                        statusLabel.setTextFill(Color.RED);
                    }
            );
        });
        Button btnBack = new Button("Back to Welcome");
        btnBack.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-underline: true;");
        btnBack.setOnAction(e -> mainApp.toWelcome());

        view.getChildren().addAll(title, UserName, passField, btnLogin, btnBack, statusLabel);
    }
    public void showMessage(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setTextFill(isError ? Color.RED : Color.LIGHTGREEN);
    }
}
