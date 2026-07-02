package divar.aut.frontend.ui;
import divar.aut.frontend.service.AdService;
import divar.aut.frontend.controller.PostAdController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import java.net.URL;
public class PostAdScreen {
    private Parent view;
    private ViewManager mainApp;
    public PostAdScreen(ViewManager mainApp, AdService adService) {
        this.mainApp = mainApp;
        buildUI(adService);
    }
    private void buildUI(AdService adService)
    {

        try{
            URL fxmlLocation = getClass().getResource("/PostAdScreen.fxml");
            if (fxmlLocation == null) {
                System.err.println("❌ فایل PostAdScreen.fxml پیدا نشد!");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            view = loader.load();
            PostAdController controller = loader.getController();
            controller.setDependencies(adService, mainApp);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("خطا در بارگذاری صفحه ثبت آگهی: " + e.getMessage());
        }
    }
    public Parent getView()
    {
        return view;
    }
}
