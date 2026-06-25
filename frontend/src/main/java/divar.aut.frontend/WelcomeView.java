package divar.aut.frontend;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WelcomeView
{
    public Scene getScene()
    {
        Label label = new Label("به سامانه خرید و فروش دست دوم خوش آمدید! 🚀");

        Button register = new Button("register");
        Button login = new Button("login");

        login.setOnAction(event -> {
            DivarApplication.showLogin();
        });

        register.setOnAction(event -> {
            DivarApplication.showRegister();
        });

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(label, login, register);

        Scene scene = new Scene(root, 350, 300);
        return  scene;

    }
}
