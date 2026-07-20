package divar.aut.frontend;

import divar.aut.frontend.ui.ThemeManager;
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
        ThemeManager.applyShellBackground(root);
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
