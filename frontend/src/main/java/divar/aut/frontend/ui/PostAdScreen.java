package divar.aut.frontend.ui;

import divar.aut.frontend.controller.PostAdController;
import divar.aut.frontend.net.AdService;
import divar.aut.frontend.model.AdDetailData;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

/**
 * UI screen class for posting or editing an advertisement.
 * <p>
 * Loads the {@code PostAdScreen.fxml} layout and initialises its controller
 * with the provided dependencies. Supports both creation of a new ad and
 * editing of an existing ad via overloaded constructors. The resulting view
 * can be retrieved via {@link #getView()}.
 * </p>
 */
public class PostAdScreen {
    private Parent view;
    private ViewManager mainApp;

    /**
     * Constructs a PostAdScreen for creating a new advertisement.
     *
     * @param mainApp   the navigation manager for screen switching.
     * @param adService the service for ad operations.
     */
    public PostAdScreen(ViewManager mainApp, AdService adService) {
        this.mainApp = mainApp;
        buildUI(adService, null);
    }

    /**
     * Constructs a PostAdScreen for editing an existing advertisement.
     * The existing ad data is pre‑populated in the form.
     *
     * @param mainApp   the navigation manager for screen switching.
     * @param adService the service for ad operations.
     * @param existingAd the existing ad data to edit.
     */
    public PostAdScreen(ViewManager mainApp, AdService adService, AdDetailData existingAd) {
        this.mainApp = mainApp;
        buildUI(adService, existingAd);
    }

    /**
     * Builds the UI by loading the FXML and setting up the controller.
     * If an existing ad is provided, it is passed to the controller for editing.
     *
     * @param adService  the service for ad operations.
     * @param existingAd the existing ad data, or null for new ad creation.
     */
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

    /**
     * Returns the loaded JavaFX root node for this screen.
     *
     * @return the {@link Parent} view to be displayed.
     */
    public Parent getView() {
        return view;
    }
}