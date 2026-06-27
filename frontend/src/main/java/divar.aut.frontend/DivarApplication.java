package divar.aut.frontend;

import divar.aut.frontend.ui.ViewManager;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.Objects;

public class DivarApplication extends Application {
    private ViewManager viewManager;
    @Override public void start(Stage primaryStage) throws Exception {
        StackPane root = new StackPane();
        ViewManager viewManager = new ViewManager(root, this);
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/background.png")));
        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO,
                        false, false, true, true)
        );
        root.setBackground(new Background(backgroundImage));

        Rectangle2D  bounds = Screen.getPrimary().getBounds();

        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        primaryStage.setScene(scene);
        viewManager.toWelcome();
        primaryStage.show();
    }
    public ViewManager getViewManager() {
        return viewManager;
    }

    public static void main(String[] args) { launch(args); }
}