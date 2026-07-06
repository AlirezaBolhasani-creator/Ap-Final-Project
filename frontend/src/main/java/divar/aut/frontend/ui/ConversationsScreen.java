package divar.aut.frontend.ui;

import divar.aut.frontend.controller.ConversationsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class ConversationsScreen {
    private Parent view;

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

    public Parent getView() {
        return view;
    }
}
