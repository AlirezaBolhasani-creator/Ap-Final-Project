package divar.aut.frontend.ui;

import divar.aut.frontend.controller.ConversationDetailController;
import divar.aut.frontend.model.ConversationData;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class ConversationDetailScreen {
    private Parent view;

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

    public Parent getView() {
        return view;
    }
}
