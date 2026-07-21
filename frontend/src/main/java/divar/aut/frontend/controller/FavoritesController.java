package divar.aut.frontend.controller;

import divar.aut.frontend.model.AdData;
import divar.aut.frontend.net.AdService;
import divar.aut.frontend.net.FavoriteService;
import divar.aut.frontend.ui.ViewManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * JavaFX controller for displaying the user's favorite advertisements.
 * Shows a grid of ad cards and allows the user to click on an ad to
 * view its details. Fetches the list of favorites via {@link FavoriteService}
 * and uses {@link AdService} for detail retrieval.
 */
public class FavoritesController {

    @FXML private FlowPane favoritesGrid;
    @FXML private Label statusLabel;

    private final FavoriteService favoriteService = new FavoriteService();
    private final AdService adService = new AdService();
    private ViewManager viewManager;

    /**
     * Injects the view manager and loads the user's favorites.
     *
     * @param viewManager the navigation manager for switching screens.
     */
    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
        loadFavorites();
    }

    private void loadFavorites() {
        statusLabel.setText("در حال دریافت علاقه‌مندی‌ها...");
        favoriteService.listFavorites(
                favorites -> {
                    statusLabel.setText(favorites.size() + " آگهی علاقه‌مندی");
                    renderFavorites(favorites);
                },
                error -> statusLabel.setText("خطا در دریافت علاقه‌مندی‌ها: " + error)
        );
    }

    private void renderFavorites(List<AdData> favorites) {
        favoritesGrid.getChildren().clear();
        if (favorites.isEmpty()) {
            Label emptyLabel = new Label("هنوز آگهی‌ای به علاقه‌مندی‌ها اضافه نکرده‌اید.");
            emptyLabel.getStyleClass().add("empty-state");
            favoritesGrid.getChildren().add(emptyLabel);
            return;
        }
        for (AdData ad : favorites) {
            Node card = buildCard(ad);
            if (card != null) favoritesGrid.getChildren().add(card);
        }
    }

    private Node buildCard(AdData ad) {
        try {
            URL cardUrl = getClass().getResource("/AdCard.fxml");
            FXMLLoader loader = new FXMLLoader(cardUrl);
            Parent card = loader.load();
            loader.<AdCardController>getController().setData(ad);
            card.setOnMouseClicked(event -> openDetails(ad));
            return card;
        } catch (IOException e) {
            statusLabel.setText("خطا در نمایش کارت آگهی");
            return null;
        }
    }

    private void openDetails(AdData ad) {
        adService.fetchAdDetails(ad.id(), detail -> Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdDetails.fxml"));
                Parent root = loader.load();
                divar.aut.frontend.ui.ThemeManager.applyCurrentMode(root);
                AdDetailsController controller = loader.getController();
                controller.setData(detail, adService, viewManager.getUserRole(), false, this::loadFavorites, viewManager);

                Stage stage = new Stage();
                stage.setTitle("جزئیات آگهی: " + detail.title());
                javafx.scene.paint.Color bg = divar.aut.frontend.ui.ThemeManager.isLightMode()
                        ? javafx.scene.paint.Color.web("#fffaf0") : javafx.scene.paint.Color.web("#0a1120");
                stage.setScene(new Scene(root, bg));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            } catch (IOException e) {
                statusLabel.setText("خطا در باز کردن جزئیات آگهی");
            }
        }), error -> Platform.runLater(() -> statusLabel.setText("خطا در دریافت جزئیات آگهی: " + error)));
    }

    /**
     * Navigates back to the main application screen.
     */
    @FXML
    private void goBack() {
        if (viewManager != null) viewManager.toMain();
    }
}