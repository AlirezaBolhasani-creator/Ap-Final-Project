package divar.aut.frontend.ui;

import divar.aut.frontend.controller.AdminDashboardController;
import divar.aut.frontend.service.AdService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

public class AdminDashboardScreen {
    private Parent view;

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
            AdminDashboardController controller = loader.getController();
            controller.setDependencies(adService, viewManager);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.err.println("Error loading AdminDashboardScreen" + e.getMessage());
        }
    }
    public  Parent getView()
    {
        return view;
    }
}
