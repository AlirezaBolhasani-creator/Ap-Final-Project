package divar.aut.frontend.ui;

import divar.aut.frontend.controller.ConversationDetailController;
import divar.aut.frontend.model.ConversationData;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * UI screen class for displaying a conversation detail view.
 * <p>
 * Loads the {@code ConversationDetailScreen.fxml} layout and initialises its
 * controller with the provided conversation data and an optional callback
 * for when a message is sent. The resulting view can be retrieved via
 * {@link #getView()} for display in a modal stage.
 * </p>
 */
public class ConversationDetailScreen {
    private Parent view;

    /**
     * Constructs the conversation detail screen by loading the FXML and
     * passing the conversation data and callback to the controller.
     *
     * @param conversation the conversation to display.
     * @param onMessageSent optional callback to run after a message is sent.
     */
    public ConversationDetailScreen(ConversationData conversation, Runnable onMessageSent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ConversationDetailScreen.fxml"));
            view = loader.load();
            ConversationDetailController controller = loader.getController();
            controller.setData(conversation, onMessageSent);
        } catch (IOException e) {
            throw new RuntimeException("Error loading ConversationDetailScreen", e);
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