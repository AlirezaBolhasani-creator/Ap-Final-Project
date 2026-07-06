package divar.aut.frontend.controller;

import divar.aut.frontend.model.AdDetailData;
import divar.aut.frontend.net.AdService;
import divar.aut.frontend.net.FavoriteService;
import divar.aut.frontend.net.ConversationService;
import divar.aut.frontend.ui.ConversationDetailScreen;
import divar.aut.frontend.ui.PostAdScreen;
import divar.aut.frontend.ui.ViewManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AdDetailsController {
    @FXML private Label titleLabel;
    @FXML private Label priceLabel;
    @FXML private Label locationLabel;
    @FXML private Label categoryLabel;
    @FXML private Label conditionLabel;
    @FXML private Label statusLabel;
    @FXML private HBox imageGalleryBox;
    @FXML private TextArea descriptionArea;
    @FXML private HBox adminActionBox;
    @FXML private HBox ownerActionBox;
    @FXML private HBox viewerActionBox;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button markAsSoldButton;
    @FXML private Button favoriteButton;
    @FXML private Button messageButton;

    private AdDetailData adDetail;
    private AdService adService;
    private final FavoriteService favoriteService = new FavoriteService();
    private final ConversationService conversationService = new ConversationService();
    private Runnable onActionCompleted;
    private ViewManager viewManager;

    public void setData(AdDetailData ad, AdService adService, String userRole,
                        boolean isOwner, Runnable onActionCompleted, ViewManager viewManager) {
        this.adDetail = ad;
        this.adService = adService;
        this.onActionCompleted = onActionCompleted;
        this.viewManager = viewManager;

        titleLabel.setText(ad.title());
        priceLabel.setText(String.valueOf(ad.price()));
        locationLabel.setText(ad.cityName());
        categoryLabel.setText(ad.categoryName());
        conditionLabel.setText(mapCondition(ad.itemCondition()));
        descriptionArea.setText(ad.description());
        statusLabel.setText(ad.status());

        renderImages(ad);

        adminActionBox.setVisible("ADMIN".equals(userRole));
        adminActionBox.setManaged("ADMIN".equals(userRole));
        ownerActionBox.setVisible(isOwner);
        ownerActionBox.setManaged(isOwner);
        boolean canFavorite = !isOwner && !"DELETED".equals(ad.status());
        viewerActionBox.setVisible(canFavorite);
        viewerActionBox.setManaged(canFavorite);
        boolean canMarkAsSold = isOwner && "ACTIVE".equals(ad.status());
        markAsSoldButton.setVisible(canMarkAsSold);
        markAsSoldButton.setManaged(canMarkAsSold);
    }

    private void renderImages(AdDetailData ad) {
        imageGalleryBox.getChildren().clear();
        if (ad.imageFileNames() == null || ad.imageFileNames().isEmpty()) {
            Label emptyImageLabel = new Label("بدون عکس");
            emptyImageLabel.setStyle("-fx-text-fill: #888; -fx-padding: 30;");
            imageGalleryBox.getChildren().add(emptyImageLabel);
            return;
        }
        for (String fileName : ad.imageFileNames()) {
            String fullUrl = ("http://localhost:8080/uploads/" + fileName).replace(" ", "%20");
            ImageView imageView = new ImageView(new Image(fullUrl, 180, 140, true, true, true));
            imageView.setPreserveRatio(true);
            StackPane frame = new StackPane(imageView);
            frame.setMinSize(190, 150);
            frame.setStyle("-fx-background-color: #111; -fx-background-radius: 8; -fx-padding: 5;");
            imageGalleryBox.getChildren().add(frame);
        }
    }

    @FXML
    private void handleMessageSeller() {
        messageButton.setDisable(true);
        conversationService.startConversation(adDetail.id(),
                conversation -> Platform.runLater(() -> {
                    ConversationDetailScreen screen = new ConversationDetailScreen(conversation, null);
                    Stage stage = new Stage();
                    stage.setTitle("گفت‌وگو: " + adDetail.title());
                    stage.setScene(new Scene(screen.getView()));
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.show();
                    messageButton.setDisable(false);
                }),
                error -> Platform.runLater(() -> {
                    showError(error);
                    messageButton.setDisable(false);
                }));
    }

    @FXML
    private void handleAddFavorite() {
        favoriteButton.setDisable(true);
        favoriteService.addFavorite(adDetail.id(),
                success -> Platform.runLater(() -> finishAction(success)),
                error -> Platform.runLater(() -> {
                    showError(error);
                    favoriteButton.setDisable(false);
                }));
    }

    @FXML
    private void handleApprove() {
        setUiDisabled(true);
        adService.updateAdStatus(adDetail.id(), "ACTIVE",
                success -> Platform.runLater(() -> finishAction(success)),
                error -> Platform.runLater(() -> showError(error)));
    }

    @FXML
    private void handleReject() {
        setUiDisabled(true);
        adService.updateAdStatus(adDetail.id(), "REJECTED",
                success -> Platform.runLater(() -> finishAction(success)),
                error -> Platform.runLater(() -> showError(error)));
    }

    @FXML
    private void handleMarkAsSold() {
        setUiDisabled(true);
        adService.markAsSold(adDetail.id(),
                success -> Platform.runLater(() -> finishAction(success)),
                error -> Platform.runLater(() -> showError(error)));
    }

    @FXML
    private void handleDelete() {
        setUiDisabled(true);
        adService.deleteAd(adDetail.id(),
                success -> Platform.runLater(() -> finishAction(success)),
                error -> Platform.runLater(() -> showError(error)));
    }

    @FXML
    private void handleEdit() {
        if (viewManager == null) return;
        try {
            PostAdScreen editScreen = new PostAdScreen(viewManager, adService, adDetail);
            viewManager.show(editScreen.getView());
            closeWindow();
        } catch (Exception e) {
            showError("خطا در باز کردن صفحه ویرایش");
        }
    }

    private void finishAction(String successMessage) {
        statusLabel.setText(successMessage);
        if (onActionCompleted != null) onActionCompleted.run();
        closeWindow();
    }

    private void showError(String error) {
        statusLabel.setText("خطا: " + error);
        setUiDisabled(false);
    }

    private void setUiDisabled(boolean disabled) {
        adminActionBox.setDisable(disabled);
        ownerActionBox.setDisable(disabled);
        if (editButton != null) editButton.setDisable(disabled);
        if (deleteButton != null) deleteButton.setDisable(disabled);
        if (markAsSoldButton != null) markAsSoldButton.setDisable(disabled);
        if (favoriteButton != null) favoriteButton.setDisable(disabled);
        if (messageButton != null) messageButton.setDisable(disabled);
    }

    private String mapCondition(String condition) {
        if (condition == null) return "نامشخص";
        return switch (condition) {
            case "NEW" -> "نو";
            case "USED" -> "کارکرده";
            default -> condition;
        };
    }

    private void closeWindow() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
}