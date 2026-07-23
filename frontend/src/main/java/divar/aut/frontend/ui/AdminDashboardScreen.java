package divar.aut.frontend.ui;

import divar.aut.frontend.controller.AdminDashboardController;
import divar.aut.frontend.net.AdService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

/**
 * UI screen class for the administrator dashboard.
 * <p>
 * Loads the {@code AdminDashboardScreen.fxml} layout and initialises its
 * controller with the provided dependencies. The resulting view can be
 * retrieved via {@link #getView()} for display in a stage or scene.
 * </p>
 */
public class AdminDashboardScreen {
    private Parent view;
    private divar.aut.frontend.controller.AdminDashboardController controller;

    /**
     * Constructs the admin dashboard screen by loading the FXML and setting up
     * the controller with the necessary dependencies.
     *
     * @param viewManager the navigation manager for screen switching.
     * @param adService   the service for advertisement operations.
     */
    public AdminDashboardScreen(ViewManager viewManager, AdService adService)
    {
        try {
            URL fxmlLocation = getClass().getResource("/AdminDashboardScreen.fxml");
            if(fxmlLocation == null){
                System.err.println("Fxml file not found");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            view = loader.load();
            controller = loader.getController();
            controller.setDependencies(adService, viewManager);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.err.println("Error loading AdminDashboardScreen" + e.getMessage());
        }
    }

    /**
     * Returns the loaded JavaFX root node for this screen.
     *
     * @return the {@link Parent} view to be displayed.
     */
    public  Parent getView()
    {
        return view;
    }

    public void selectAllAdsTab() {
        if (controller != null) controller.selectAllAdsTab();
    }
}