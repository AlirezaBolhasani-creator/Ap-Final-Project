package divar.aut.frontend;

import javafx.application.Application;
import javafx.stage.Stage;

public class DivarApplication extends Application {
    private static Stage mainStage;

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        mainStage.setTitle("سامانه دیوار");
        showWelcome();
        mainStage.show();
    }

    public static void showWelcome() {
        mainStage.setScene(new WelcomeView().getScene());
    }

    public static void showLogin() {
        mainStage.setScene(new LoginView().getScene());
    }

    public static void showRegister() {
        mainStage.setScene(new RegisterView().getScene());
    }

    public static void main(String[] args) { launch(args); }
}