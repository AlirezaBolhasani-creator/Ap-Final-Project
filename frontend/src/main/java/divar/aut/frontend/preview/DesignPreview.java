package divar.aut.frontend.preview;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Standalone launcher for the design-system component gallery.
 * Run to preview theme.css components without starting the full app:
 *   mvn -pl frontend javafx:run -Djavafx.mainClass=divar.aut.frontend.preview.DesignPreview
 */
public class DesignPreview extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/DesignPreview.fxml"));
        Scene scene = new Scene(root, 1200, 860);
        stage.setTitle("Divar — Design System Preview");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
