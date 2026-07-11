package divar.aut.frontend.controller;

import divar.aut.frontend.model.AdDetailData;
import divar.aut.frontend.net.AdService;
import divar.aut.frontend.net.FavoriteService;
import divar.aut.frontend.net.ConversationService;
import divar.aut.frontend.net.RatingService;
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
import javafx.scene.layout.VBox;
import javafx.geometry.NodeOrientation;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class AdDetailsController {
    @FXML private Label titleLabel;
    @FXML private Label priceLabel;
    @FXML private Label locationLabel;
    @FXML private Label categoryLabel;
    @FXML private Label conditionLabel;
    @FXML private Label statusLabel;
    @FXML private Label sellerRatingLabel;
    @FXML private Label sellerNameLabel;
    @FXML private HBox imageGalleryBox;
    @FXML private Label descriptionLabel;
    @FXML private HBox adminActionBox;
    @FXML private HBox ownerActionBox;
    @FXML private HBox viewerActionBox;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button markAsSoldButton;
    @FXML private Button favoriteButton;
    @FXML private Button messageButton;
    @FXML private Button ratingButton;
    @FXML private ImageView mainImageView;
    @FXML private VBox imageContainer;

    private AdDetailData adDetail;
    private AdService adService;
    private final FavoriteService favoriteService = new FavoriteService();
    private final ConversationService conversationService = new ConversationService();
    private final RatingService ratingService = new RatingService();
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
        descriptionLabel.setText(ad.description());
        statusLabel.setText(ad.status());
        sellerNameLabel.setText("فروشنده: " + ad.seller().fullname() + " (@" + ad.seller().username() + ")");
        sellerRatingLabel.setText(ad.ratingCount() == 0
                ? "بدون امتیاز"
                : String.format("%.1f از 5 (%d نظر)", ad.averageRating(), ad.ratingCount()));

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
            imageContainer.setVisible(false);
            imageContainer.setManaged(false);
            return;
        }

        imageContainer.setVisible(true);
        imageContainer.setManaged(true);

        String firstImageUrl = "http://localhost:8080/uploads/" + ad.imageFileNames().get(0);
        mainImageView.setImage(new Image(firstImageUrl));

        for (String fileName : ad.imageFileNames()) {
            String fullUrl = ("http://localhost:8080/uploads/" + fileName).replace(" ", "%20");
            ImageView thumb = new ImageView(new Image(fullUrl, 80, 80, true, true, true));
            thumb.setPreserveRatio(true);

            StackPane thumbFrame = new StackPane(thumb);
            thumbFrame.getStyleClass().add("thumbnail-frame");

            thumbFrame.setOnMouseClicked(e -> mainImageView.setImage(new Image(fullUrl)));
            imageGalleryBox.getChildren().add(thumbFrame);
        }
    }

    @FXML
    private void handleRateSeller() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("ثبت امتیاز فروشنده");
        dialog.setHeaderText("تجربه شما از معامله با " + adDetail.seller().fullname());

        ComboBox<Integer> scoreCombo = new ComboBox<>();
        scoreCombo.getItems().addAll(1, 2, 3, 4, 5);
        scoreCombo.getSelectionModel().select(Integer.valueOf(5));
        scoreCombo.getStyleClass().add("rating-combo");

        TextArea commentArea = new TextArea();
        commentArea.setPromptText("نظر شما درباره پاسخ‌گویی و نحوه معامله (اختیاری)");
        commentArea.setWrapText(true);
        commentArea.setPrefRowCount(4);

        FontIcon starIcon = new FontIcon("fas-star");
        starIcon.setIconColor(javafx.scene.paint.Color.web("#facc15"));
        starIcon.setIconSize(22);
        Label scoreLabel = new Label("امتیاز از ۱ تا ۵");
        scoreLabel.getStyleClass().add("dialog-label");
        HBox scoreRow = new HBox(10, starIcon, scoreLabel, scoreCombo);
        scoreRow.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        Label commentLabel = new Label("توضیح کوتاه");
        commentLabel.getStyleClass().add("dialog-label");
        VBox content = new VBox(12, scoreRow, commentLabel, commentArea);
        content.getStyleClass().add("dialog-content");
        content.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        dialog.getDialogPane().setContent(content);
        ButtonType submitRating = new ButtonType("ثبت امتیاز", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("انصراف", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(submitRating, cancel);
        styleDialog(dialog);

        dialog.showAndWait().filter(submitRating::equals).ifPresent(button -> {
            ratingButton.setDisable(true);
            ratingService.submitRating(adDetail.id(), scoreCombo.getValue(), commentArea.getText().trim(),
                    rating -> Platform.runLater(() -> {
                        sellerRatingLabel.setText("امتیاز ثبت شد");
                        ratingButton.setDisable(true);
                        statusLabel.setText("امتیاز شما با موفقیت ثبت شد");
                    }),
                    error -> Platform.runLater(() -> {
                        showError(error);
                        ratingButton.setDisable(false);
                    }));
        });
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

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("رد آگهی");
        dialog.setHeaderText("دلیل رد شدن «" + adDetail.title() + "»");

        Label guidance = new Label("دلیل واضحی بنویسید تا آگهی‌دهنده بتواند مشکل را اصلاح کند.");
        guidance.setWrapText(true);
        guidance.getStyleClass().add("dialog-label");
        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("برای مثال: تصاویر یا توضیحات آگهی با قوانین مطابقت ندارد");
        reasonArea.setWrapText(true);
        reasonArea.setPrefRowCount(5);
        reasonArea.getStyleClass().add("danger-field");

        VBox content = new VBox(12, guidance, reasonArea);
        content.getStyleClass().add("dialog-content");
        content.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        dialog.getDialogPane().setContent(content);
        ButtonType reject = new ButtonType("رد آگهی", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("انصراف", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(reject, cancel);
        styleDialog(dialog);

        Optional<String> result = dialog.showAndWait()
                .filter(reject::equals)
                .map(button -> reasonArea.getText());

        result.ifPresent(reason -> {
            if (reason.trim().isEmpty()) {
                showError("برای رد آگهی، وارد کردن دلیل الزامی است.");
                return;
            }

            setUiDisabled(true);

            adService.rejectAd(adDetail.id(), reason,
                    success -> Platform.runLater(() -> finishAction(success)),
                    error -> Platform.runLater(() -> showError(error)));
        });
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

    private void styleDialog(Dialog<?> dialog) {
        dialog.initOwner(titleLabel.getScene().getWindow());
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/Dialog.css").toExternalForm());
        dialog.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
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
        if (ratingButton != null) ratingButton.setDisable(disabled);
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