package divar.aut.frontend.ui;

import divar.aut.frontend.controller.FavoritesController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

public class FavoritesScreen {
    private Parent view;

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

    public Parent getView() {
        return view;
    }
}
