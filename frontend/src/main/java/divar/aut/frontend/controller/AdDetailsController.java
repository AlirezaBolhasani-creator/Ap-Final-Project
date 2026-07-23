package divar.aut.frontend.controller;

import divar.aut.frontend.model.AdDetailData;
import divar.aut.frontend.model.ConversationData;
import divar.aut.frontend.model.RatingData;
import divar.aut.frontend.net.AdService;
import divar.aut.frontend.net.FavoriteService;
import divar.aut.frontend.net.ConversationService;
import divar.aut.frontend.net.RatingService;
import divar.aut.frontend.util.PriceFormatter;
import divar.aut.frontend.SessionManager;
import divar.aut.frontend.ui.PostAdScreen;
import divar.aut.frontend.ui.ThemeManager;
import divar.aut.frontend.ui.ViewManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.geometry.NodeOrientation;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

/**
 * JavaFX controller for the advertisement details view.
 * <p>
 * Displays full ad information including images, description, seller details,
 * and ratings. Provides actions based on user role (owner, admin, or viewer):
 * edit, delete, mark as sold, add to favorites, message seller, rate seller,
 * approve/reject (admin), and delete comments (admin).
 * </p>
 * <p>
 * The controller interacts with backend services ({@link AdService}, etc.)
 * and manages UI state (enabling/disabling buttons, showing errors).
 * </p>
 */
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
    @FXML private Button approveButton;
    @FXML private Button rejectButton;
    @FXML private Button adminDeleteButton;
    @FXML private VBox commentsSection;
    @FXML private VBox commentsBox;
    @FXML private ImageView mainImageView;
    @FXML private VBox imageContainer;

    private AdDetailData adDetail;
    private AdService adService;
    private final FavoriteService favoriteService = new FavoriteService();
    private final ConversationService conversationService = new ConversationService();
    private final RatingService ratingService = new RatingService();
    private Runnable onActionCompleted;
    private java.util.function.Consumer<Long> adminDeleteHandler;
    private ViewManager viewManager;
    private boolean isAdmin;
    private Runnable backAction;

    /**
     * Populates the UI with the provided ad details.
     * <p>
     * This overload is used when no custom admin delete handler is needed.
     * </p>
     *
     * @param ad                 the ad detail data.
     * @param adService          the service for ad operations.
     * @param userRole           the role of the currently logged-in user ("ADMIN" or "USER").
     * @param isOwner            whether the current user is the owner of the ad.
     * @param onActionCompleted  callback to run after a successful action (e.g., refresh list).
     * @param viewManager        the navigation manager for switching screens.
     */
    public void setData(AdDetailData ad, AdService adService, String userRole,
                        boolean isOwner, Runnable onActionCompleted, ViewManager viewManager) {
        setData(ad, adService, userRole, isOwner, onActionCompleted, viewManager, null, null);
    }

    /**
     * Populates the UI with the provided ad details and an admin delete handler.
     * <p>
     * Renders ad information, images, comments, and configures action buttons
     * based on the user's role and the ad's status.
     * </p>
     *
     * @param ad                 the ad detail data.
     * @param adService          the service for ad operations.
     * @param userRole           the role of the currently logged-in user ("ADMIN" or "USER").
     * @param isOwner            whether the current user is the owner of the ad.
     * @param onActionCompleted  callback to run after a successful action.
     * @param viewManager        the navigation manager for switching screens.
     * @param adminDeleteHandler callback to handle admin deletion (receives ad ID).
     */
    public void setData(AdDetailData ad, AdService adService, String userRole,
                        boolean isOwner, Runnable onActionCompleted, ViewManager viewManager,
                        java.util.function.Consumer<Long> adminDeleteHandler) {
        setData(ad, adService, userRole, isOwner, onActionCompleted, viewManager, adminDeleteHandler, null);
    }

    /**
     * Populates the UI with the provided ad details, an admin delete handler, and
     * an explicit navigation target for the back button / post-action return.
     * <p>
     * Renders ad information, images, comments, and configures action buttons
     * based on the user's role and the ad's status.
     * </p>
     *
     * @param ad                 the ad detail data.
     * @param adService          the service for ad operations.
     * @param userRole           the role of the currently logged-in user ("ADMIN" or "USER").
     * @param isOwner            whether the current user is the owner of the ad.
     * @param onActionCompleted  callback to run after a successful action.
     * @param viewManager        the navigation manager for switching screens.
     * @param adminDeleteHandler callback to handle admin deletion (receives ad ID).
     * @param backAction         where to navigate when the user presses back or an
     *                           action completes, i.e. the screen this view was
     *                           actually opened from. When {@code null}, falls back
     *                           to the previous role-based default (admin dashboard
     *                           for admins, main screen otherwise) for callers that
     *                           haven't been updated to pass an explicit origin.
     */
    public void setData(AdDetailData ad, AdService adService, String userRole,
                        boolean isOwner, Runnable onActionCompleted, ViewManager viewManager,
                        java.util.function.Consumer<Long> adminDeleteHandler, Runnable backAction) {
        this.adDetail = ad;
        this.adService = adService;
        this.onActionCompleted = onActionCompleted;
        this.viewManager = viewManager;
        this.adminDeleteHandler = adminDeleteHandler;
        this.backAction = backAction;

        titleLabel.setText(ad.title());
        priceLabel.setText(PriceFormatter.format(ad.price()));
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

        this.isAdmin = "ADMIN".equals(userRole);
        boolean isAdmin = this.isAdmin;
        String status = ad.status();
        adminActionBox.setVisible(isAdmin);
        adminActionBox.setManaged(isAdmin);
        if (isAdmin) {
            boolean pending = "PENDING_REVIEW".equals(status);
            approveButton.setVisible(pending);
            approveButton.setManaged(pending);
            rejectButton.setVisible(pending);
            rejectButton.setManaged(pending);
            boolean deletable = !"DELETED".equals(status);
            adminDeleteButton.setVisible(deletable);
            adminDeleteButton.setManaged(deletable);
        }
        ownerActionBox.setVisible(isOwner);
        ownerActionBox.setManaged(isOwner);
        boolean canFavorite = !isOwner && !"DELETED".equals(status);
        viewerActionBox.setVisible(canFavorite);
        viewerActionBox.setManaged(canFavorite);
        boolean canMarkAsSold = isOwner && "ACTIVE".equals(status);
        markAsSoldButton.setVisible(canMarkAsSold);
        markAsSoldButton.setManaged(canMarkAsSold);

        renderComments(ad.ratings());
    }

    private void renderComments(List<RatingData> ratings) {
        if (commentsBox == null || commentsSection == null) return;
        commentsBox.getChildren().clear();
        List<RatingData> withComments = ratings == null ? List.of() :
                ratings.stream()
                        .filter(rating -> rating.comment() != null && !rating.comment().isBlank())
                        .toList();
        if (withComments.isEmpty()) {
            commentsSection.setVisible(false);
            commentsSection.setManaged(false);
            return;
        }
        commentsSection.setVisible(true);
        commentsSection.setManaged(true);
        for (RatingData rating : withComments) {
            Label header = new Label("@" + rating.buyerUsername() + "  ·  " + rating.score() + "/5");
            header.getStyleClass().addAll("text-small", "comment-head");
            header.setMaxWidth(Double.MAX_VALUE);
            javafx.scene.layout.HBox.setHgrow(header, javafx.scene.layout.Priority.ALWAYS);

            javafx.scene.layout.HBox headerRow = new javafx.scene.layout.HBox(8, header);
            headerRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            if (isAdmin) {
                javafx.scene.control.Button deleteBtn = new javafx.scene.control.Button("حذف");
                deleteBtn.getStyleClass().addAll("btn", "btn-danger", "btn-sm");
                deleteBtn.setOnAction(event -> deleteComment(rating));
                headerRow.getChildren().add(deleteBtn);
            }

            Label body = new Label(rating.comment());
            body.setWrapText(true);
            body.getStyleClass().add("text-caption");
            VBox card = new VBox(4, headerRow, body);
            card.getStyleClass().add("comment-card");
            commentsBox.getChildren().add(card);
        }
    }

    private void deleteComment(RatingData rating) {
        ratingService.deleteRating(rating.id(),
                () -> javafx.application.Platform.runLater(() -> {
                    statusLabel.setText("نظر حذف شد");
                    statusLabel.getStyleClass().setAll("status-success");
                    renderComments(adDetail.ratings().stream()
                            .filter(r -> !r.id().equals(rating.id()))
                            .toList());
                }),
                error -> javafx.application.Platform.runLater(() -> {
                    statusLabel.setText("خطا در حذف نظر: " + error);
                    statusLabel.getStyleClass().setAll("status-danger");
                }));
    }

    @FXML
    private void handleBack() {
        if (viewManager == null) return;
        if (backAction != null) {
            backAction.run();
        } else if (isAdmin) {
            viewManager.toAdminDashboard();
        } else {
            viewManager.toMain();
        }
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

    /**
     * Opens a dialog for the user to rate the seller.
     * The user selects a score (1–5) and optionally adds a comment.
     */
    @FXML
    private void handleRateSeller() {
        String currentUser = SessionManager.getInstance().getUsername();
        if (currentUser != null && currentUser.equals(adDetail.seller().username())) {
            showError("شما فروشنده این آگهی هستید و نمی‌توانید امتیاز ثبت کنید.");
            return;
        }

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

    /**
     * Initiates a conversation with the seller.
     * Loads the conversation detail screen inside the main app view.
     */
    @FXML
    private void handleMessageSeller() {
        String currentUser = SessionManager.getInstance().getUsername();
        if (currentUser != null && currentUser.equals(adDetail.seller().username())) {
            showError("شما فروشنده این آگهی هستید و نمی‌توانید پیام ارسال کنید.");
            return;
        }
        messageButton.setDisable(true);
        conversationService.listConversations(conversations -> Platform.runLater(() -> {
            try {
                URL fxmlUrl = getClass().getResource("/ConversationDetailScreen.fxml");
                if (fxmlUrl == null) return;
                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                Parent view = loader.load();
                ConversationDetailController controller = loader.getController();
                controller.setViewManager(viewManager);
                Optional<ConversationData> existing = conversations.stream()
                        .filter(c -> c.adId().equals(adDetail.id()))
                        .findFirst();
                Runnable returnToAd = backAction;
                if (existing.isPresent()) {
                    controller.setData(existing.get(), null, returnToAd);
                } else {
                    controller.setData(adDetail.id(), adDetail.title(), null, returnToAd);
                }
                viewManager.show(view);
            } catch (IOException e) {
                e.printStackTrace();
                showError("خطا در باز کردن گفت‌وگو");
            } finally {
                messageButton.setDisable(false);
            }
        }), error -> Platform.runLater(() -> {
            showError(error);
            messageButton.setDisable(false);
        }));
    }

    /**
     * Adds the ad to the current user's favorites.
     */
    @FXML
    private void handleAddFavorite() {
        favoriteButton.setDisable(true);
        favoriteService.addFavorite(adDetail.id(),
                success -> Platform.runLater(() -> {
                    statusLabel.getStyleClass().removeAll("status-danger");
                    if (!statusLabel.getStyleClass().contains("status-success")) {
                        statusLabel.getStyleClass().add("status-success");
                    }
                    statusLabel.setText(success);
                    if (onActionCompleted != null) onActionCompleted.run();
                }),
                error -> Platform.runLater(() -> {
                    showError(error);
                    favoriteButton.setDisable(false);
                }));
    }

    /**
     * Admin action: approves the ad (sets status to ACTIVE).
     */
    @FXML
    private void handleApprove() {
        setUiDisabled(true);
        adService.updateAdStatus(adDetail.id(), "ACTIVE",
                success -> Platform.runLater(() -> finishAction(success)),
                error -> Platform.runLater(() -> showError(error)));
    }

    /**
     * Admin action: deletes the ad entirely (hard delete).
     * Uses the provided adminDeleteHandler callback.
     */
    @FXML
    private void handleAdminDelete() {
        if (adminDeleteHandler == null) {
            showError("امکان حذف آگهی از اینجا وجود ندارد");
            return;
        }
        setUiDisabled(true);
        adminDeleteHandler.accept(adDetail.id());
        returnToPreviousScreen();
    }

    /**
     * Admin action: rejects the ad with a required reason.
     * Opens a dialog for the admin to enter the rejection reason.
     */
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

    /**
     * Owner action: marks the ad as sold (status changes to SOLD).
     */
    @FXML
    private void handleMarkAsSold() {
        setUiDisabled(true);
        adService.markAsSold(adDetail.id(),
                success -> Platform.runLater(() -> finishAction(success)),
                error -> Platform.runLater(() -> showError(error)));
    }

    /**
     * Owner action: soft‑deletes the ad (status changed to DELETED).
     */
    @FXML
    private void handleDelete() {
        setUiDisabled(true);
        adService.deleteAd(adDetail.id(),
                success -> Platform.runLater(() -> finishAction(success)),
                error -> Platform.runLater(() -> showError(error)));
    }

    /**
     * Owner action: opens the edit screen for this ad.
     */
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
                getClass().getResource("/theme.css").toExternalForm());
        ThemeManager.applyCurrentMode(dialog.getDialogPane());
        dialog.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
    }

    private void finishAction(String successMessage) {
        statusLabel.setText(successMessage);
        if (onActionCompleted != null) onActionCompleted.run();
        returnToPreviousScreen();
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
        if (approveButton != null) approveButton.setDisable(disabled);
        if (rejectButton != null) rejectButton.setDisable(disabled);
        if (adminDeleteButton != null) adminDeleteButton.setDisable(disabled);
    }

    private String mapCondition(String condition) {
        if (condition == null) return "نامشخص";
        return switch (condition) {
            case "NEW" -> "نو";
            case "USED" -> "کارکرده";
            default -> condition;
        };
    }

    /**
     * Closes this view's window, but only when it's a real standalone popup
     * {@link Stage} (e.g. opened from {@code FavoritesController}). When the
     * view is embedded in the shared application window instead, closing
     * that window would close the whole app, so this is a no-op — the
     * caller is expected to navigate away via {@link #returnToPreviousScreen()}
     * or by showing another view directly (see {@link #handleEdit()}).
     */
    private void closeWindow() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        if (viewManager != null && stage == viewManager.getPrimaryStage()) {
            return;
        }
        stage.close();
    }

    /**
     * Returns to whatever screen should be visible after an action
     * completes. When embedded in the shared application window, this
     * navigates back to the explicit {@link #backAction} for this view if
     * one was supplied via {@code setData}, i.e. the screen it was actually
     * opened from; otherwise it falls back via {@link ViewManager} (to the
     * admin dashboard for admins, or the main screen otherwise). When shown
     * in its own popup {@link Stage} instead, it simply closes that popup.
     */
    private void returnToPreviousScreen() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        if (viewManager != null && stage == viewManager.getPrimaryStage()) {
            if (backAction != null) {
                backAction.run();
            } else if (isAdmin) {
                viewManager.toAdminDashboard();
            } else {
                viewManager.toMain();
            }
            return;
        }
        stage.close();
    }
}