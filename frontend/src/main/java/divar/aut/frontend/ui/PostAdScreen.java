package divar.aut.frontend.ui;

import divar.aut.frontend.controller.PostAdController;
import divar.aut.frontend.net.AdService;
import divar.aut.frontend.model.AdDetailData;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

public class PostAdScreen {
    private Parent view;
    private ViewManager mainApp;

    public PostAdScreen(ViewManager mainApp, AdService adService) {
        this.mainApp = mainApp;
        buildUI(adService, null);
    }

    public PostAdScreen(ViewManager mainApp, AdService adService, AdDetailData existingAd) {
        this.mainApp = mainApp;
        buildUI(adService, existingAd);
    }

    private void buildUI(AdService adService, AdDetailData existingAd) {
        try {
            URL fxmlLocation = getClass().getResource("/PostAdScreen.fxml");
            if (fxmlLocation == null) {
                System.err.println("❌ فایل PostAdScreen.fxml پیدا نشد!");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            view = loader.load();
            PostAdController controller = loader.getController();
            controller.setDependencies(adService, mainApp);
            if (existingAd != null) {
                controller.setData(existingAd);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("خطا در بارگذاری صفحه ثبت آگهی: " + e.getMessage());
        }
    }

    public Parent getView() {
        return view;
    }
}
