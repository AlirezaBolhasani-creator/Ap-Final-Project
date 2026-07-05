package divar.aut.frontend.controller;

import divar.aut.frontend.model.AdData;
import divar.aut.frontend.net.AdService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class AdDetailsController {
    @FXML private Label titleLabel, priceLabel, locationLabel, categoryLabel, conditionLabel, statusLabel;
    @FXML private ImageView adImageView;
    @FXML private TextArea descriptionArea;
    @FXML private HBox adminActionBox;

    private AdData adData;
    private AdService adService;
    private Runnable onActionCompleted;

    public void setData(AdData ad, AdService adService, String userRole, Runnable onActionCompleted) {
        this.adData = ad;
        this.adService = adService;
        this.onActionCompleted = onActionCompleted;

        titleLabel.setText(ad.title());
        priceLabel.setText(String.valueOf(ad.price()));
        locationLabel.setText(ad.location());
        categoryLabel.setText(ad.category());
        conditionLabel.setText(ad.condition());
        descriptionArea.setText(ad.imageUrl());

        if (ad.imageUrl() != null && !ad.imageUrl().isBlank()) {
            String fullUrl = "http://localhost:8080/uploads/" + ad.imageUrl().replace(" ", "%20");
            try { adImageView.setImage(new Image(fullUrl, true)); } catch (Exception ignored) {}
        }


        if ("ADMIN".equals(userRole)) {
            adminActionBox.setVisible(true);
            adminActionBox.setManaged(true);
        }
    }

    @FXML
    private void handleApprove() { updateStatus("ACTIVE"); }

    @FXML
    private void handleReject() { updateStatus("REJECTED"); }

    private void updateStatus(String status) {
        adminActionBox.setDisable(true);
        adService.updateAdStatus(adData.id(), status,
                success -> Platform.runLater(() -> {
                    if (onActionCompleted != null) onActionCompleted.run();
                    closeWindow();
                }),
                error -> Platform.runLater(() -> {
                    statusLabel.setText("خطا: " + error);
                    adminActionBox.setDisable(false);
                })
        );
    }

    private void closeWindow() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
}