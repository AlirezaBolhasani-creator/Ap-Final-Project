package divar.aut.frontend.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class WelcomeScreen
{
    private VBox view;
    private ViewManager mainApp;
    public WelcomeScreen(ViewManager mainApp)
    {
        this.mainApp = mainApp;
        buildUI();

    }
    private void buildUI()
    {
        view = new VBox(12);
        view.setMaxSize(380, 250);
        view.setPadding(new Insets(40));
        view.setAlignment(Pos.CENTER);
        view.setStyle(getGlassStyle());

        Label welcomeLabel = new Label("WELCOME TO DIVAR");
        welcomeLabel.setTextFill(Color.WHITE);
        welcomeLabel.setFont(Font.font("System", 20));

        Button btnGoLogin = createStyledButton("Login");
        btnGoLogin.setOnAction(e -> mainApp.toLogin());

        Button btnGoRegister = createStyledButton("Register");
        btnGoRegister.setOnAction(e -> mainApp.toRegister());

        view.getChildren().addAll(welcomeLabel, btnGoLogin, btnGoRegister);
    }
    public static Button createStyledButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: rgba(255,255,255,0.8); -fx-background-radius: 20;");
        return btn;
    }
    public static String getGlassStyle() {
        return "-fx-background-color: rgba(255, 255, 255, 0.2);" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: rgba(255, 255, 255, 0.3);" +
                "-fx-border-radius: 15;" +
                "-fx-border-width: 1;";
    }
    public VBox getView()
    {
        return view;
    }
}
