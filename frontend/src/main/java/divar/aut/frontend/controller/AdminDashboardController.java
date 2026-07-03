package divar.aut.frontend.controller;

import divar.aut.frontend.model.AdData;
import divar.aut.frontend.service.AdService;
import divar.aut.frontend.ui.ViewManager;
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

public class AdminDashboardController {

    @FXML private FlowPane adminAdGrid;
    @FXML private Label statusLabel;

    private AdService adService;
    private ViewManager viewManager;

    public void setDependencies(AdService adService, ViewManager viewManager) {
        this.adService = adService;
        this.viewManager = viewManager;
        loadPendingAds();
    }

    private void loadPendingAds() {
        statusLabel.setText("در حال دریافت آگهی‌های بررسی نشده...");
        statusLabel.setStyle("-fx-text-fill: #aaa;");

        // Disable grid while loading
        adminAdGrid.setDisable(true);

        adService.fetchPendingAds(
                ads -> {
                    statusLabel.setText(ads.size() + " آگهی در انتظار بررسی یافت شد.");
                    statusLabel.setStyle("-fx-text-fill: #4ade80;");
                    adminAdGrid.setDisable(false);
                    renderAds(ads);
                },
                error -> {
                    statusLabel.setText("خطا در دریافت آگهی‌ها: " + error);
                    statusLabel.setStyle("-fx-text-fill: #ff5a5a;");
                    adminAdGrid.setDisable(false);
                }
        );
    }

    private void renderAds(List<AdData> ads) {
        adminAdGrid.getChildren().clear();

        if (ads.isEmpty()) {
            Label emptyLabel = new Label("هیچ آگهی در انتظار بررسی وجود ندارد.");
            emptyLabel.setStyle("-fx-text-fill: #777; -fx-font-size: 14px;");
            adminAdGrid.getChildren().add(emptyLabel);
            return;
        }

        // Generate a card for each ad
        for (AdData ad : ads) {
            Node card = buildCard(ad);
            if (card != null) {
                adminAdGrid.getChildren().add(card);
            }
        }
    }

    private Node buildCard(AdData data) {
        try {
            URL cardUrl = getClass().getResource("/AdCard.fxml");
            FXMLLoader loader = new FXMLLoader(cardUrl);
            Parent card = loader.load();

            // Set the data into the card UI
            AdCardController ctrl = loader.getController();
            ctrl.setData(data);

            // Add hover effect
            card.setOnMouseEntered(e -> {
                card.setScaleX(1.03);
                card.setScaleY(1.03);
                card.setStyle("-fx-effect:dropshadow(gaussian,rgba(239,63,63,0.3),24,0.1,0,6);");
            });
            card.setOnMouseExited(e -> {
                card.setScaleX(1.0);
                card.setScaleY(1.0);
                card.setStyle("");
            });

            // Handle Click: Open Details Popup
            card.setOnMouseClicked(e -> openAdDetails(data));

            return card;
        } catch (IOException e) {
            System.err.println("Error loading AdCard.fxml in AdminDashboard");
            e.printStackTrace();
            return null;
        }
    }

    private void openAdDetails(AdData data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdDetails.fxml"));
            Parent root = loader.load();

            AdDetailsController controller = loader.getController();

            // Pass the data, explicitly state "ADMIN" role, and pass a callback to refresh the grid
            controller.setData(data, adService, "ADMIN", () -> {
                // This callback runs after the admin clicks approve or reject in the popup
                loadPendingAds();
            });

            Stage popupStage = new Stage();
            popupStage.setTitle("بررسی آگهی: " + data.title());
            popupStage.setScene(new Scene(root));

            // This forces the user to interact with the popup before clicking other things
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.show();

        } catch (IOException ex) {
            System.err.println("Error loading AdDetails.fxml");
            ex.printStackTrace();
        }
    }

    @FXML
    private void goBack() {
        viewManager.toMain();
    }
}