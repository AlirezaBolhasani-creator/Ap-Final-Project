package divar.aut.frontend.ui;

import divar.aut.frontend.controller.ConversationsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * UI screen class for displaying the list of user conversations.
 * <p>
 * Loads the {@code ConversationsScreen.fxml} layout and initialises its
 * controller with the provided {@link ViewManager}. The resulting view
 * can be retrieved via {@link #getView()} for display in a stage or scene.
 * </p>
 */
public class ConversationsScreen {
    private Parent view;

    /**
     * Constructs the conversations screen by loading the FXML and
     * passing the view manager to the controller.
     *
     * @param viewManager the navigation manager for screen switching.
     * @throws RuntimeException if the FXML file cannot be loaded.
     */
    public ConversationsScreen(ViewManager viewManager) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ConversationsScreen.fxml"));
            view = loader.load();
            ConversationsController controller = loader.getController();
            controller.setViewManager(viewManager);
        } catch (IOException e) {
            throw new RuntimeException("Error loading ConversationsScreen", e);
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