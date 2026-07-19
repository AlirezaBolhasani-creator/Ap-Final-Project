package divar.aut.frontend;

import divar.aut.frontend.ui.ViewManager;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class DivarApplication extends Application {
    private ViewManager viewManager;

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0c1830, #0a1120 60%, #0a1120);");
        viewManager = new ViewManager(root, this);

        Rectangle2D bounds = Screen.getPrimary().getBounds();
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        primaryStage.setScene(scene);
        viewManager.toWelcome();
        primaryStage.show();
    }

    public ViewManager getViewManager() {
        return viewManager;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
