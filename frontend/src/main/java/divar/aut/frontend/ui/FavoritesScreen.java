package divar.aut.frontend.ui;

import divar.aut.frontend.controller.FavoritesController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

/**
 * UI screen class for displaying the user's favorite advertisements.
 * <p>
 * Loads the {@code FavoritesScreen.fxml} layout and initialises its
 * controller with the provided {@link ViewManager}. The resulting view
 * can be retrieved via {@link #getView()} for display in a stage or scene.
 * </p>
 */
public class FavoritesScreen {
    private Parent view;

    /**
     * Constructs the favorites screen by loading the FXML and
     * passing the view manager to the controller.
     *
     * @param viewManager the navigation manager for screen switching.
     * @throws RuntimeException if the FXML file cannot be loaded.
     */
    public FavoritesScreen(ViewManager viewManager) {
        try {
            URL fxmlLocation = getClass().getResource("/FavoritesScreen.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            view = loader.load();
            FavoritesController controller = loader.getController();
            controller.setViewManager(viewManager);
        } catch (IOException e) {
            throw new RuntimeException("Error loading FavoritesScreen", e);
        }
    }

    /**
     * Returns the loaded JavaFX root node for this screen.
     *
     * @return the {@link Parent} view to be displayed.
     */
    public Parent getView() {
        return view;
    }
}