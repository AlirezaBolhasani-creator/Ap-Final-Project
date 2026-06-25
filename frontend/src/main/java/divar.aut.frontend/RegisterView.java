package divar.aut.frontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class RegisterView
{
    public Scene getScene()
    {
        Label label = new Label("بخش ثبت نام در سامانه!");

        TextField name = new TextField();
        name.setPromptText("name");

        TextField username = new TextField();
        username.setPromptText("username");

        TextField password = new TextField();
        password.setPromptText("password");

        TextField email = new TextField();
        email.setPromptText("email");

        TextField phone = new TextField();
        phone.setPromptText("phone number");
        Label statusLabel = new Label("");

        Button register = new Button("Register");
        register.setOnAction(event -> {

        });

        Button back = new Button("Back");
        back.setOnAction(event -> {DivarApplication.showWelcome();});

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(label, name,  username, password, email, phone, register, back, statusLabel);

        Scene scene = new Scene(root, 350, 300);
        return scene;
    }
}
