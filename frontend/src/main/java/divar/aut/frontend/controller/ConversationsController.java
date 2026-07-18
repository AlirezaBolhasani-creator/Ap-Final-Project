package divar.aut.frontend.controller;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import divar.aut.frontend.model.ConversationData;
import divar.aut.frontend.net.ConversationService;
import divar.aut.frontend.ui.ConversationDetailScreen;
import divar.aut.frontend.ui.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class ConversationsController {

    @FXML private VBox conversationList;
    @FXML private Label statusLabel;

    private final ConversationService conversationService = new ConversationService();
    private ViewManager viewManager;

    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
        loadConversations();
    }

    private void loadConversations() {
        statusLabel.setText("در حال دریافت گفت‌وگوها...");
        conversationService.listConversations(
                conversations -> {
                    statusLabel.setText(conversations.size() + " گفت‌وگو");
                    renderConversations(conversations);
                },
                error -> statusLabel.setText("خطا در دریافت گفت‌وگوها: " + error)
        );
    }

    private void renderConversations(List<ConversationData> conversations) {
        conversationList.getChildren().clear();
        if (conversations.isEmpty()) {
            Label emptyLabel = new Label("هنوز گفت‌وگویی ندارید.");
            emptyLabel.setStyle("-fx-text-fill: #777; -fx-font-size: 14px;");
            conversationList.getChildren().add(emptyLabel);
            return;
        }
        for (ConversationData conversation : conversations) {
            VBox card = conversationCard(conversation);
            conversationList.getChildren().add(card);
        }
    }

    private Label adminBadge() {
        FontIcon icon = new FontIcon("fas-shield-alt");
        icon.setIconSize(9);
        icon.setIconColor(javafx.scene.paint.Color.web("#ff6b6b"));
        Label badge = new Label("ادمین", icon);
        badge.setStyle("-fx-background-color: rgba(239,63,63,0.18); -fx-text-fill: #ff6b6b; "
                        + "-fx-padding: 1 8; -fx-background-radius: 10; -fx-font-size: 10px; -fx-font-weight: bold;");
        return badge;
    }

    private HBox partyNode(String role, String username, boolean isAdmin) {
        Label label = new Label(isAdmin ? role + ": ادمین" : role + ": " + username);
        label.setStyle(isAdmin ? "-fx-text-fill: #ff6b6b;" : "-fx-text-fill: #aaa;");
        HBox box = isAdmin ? new HBox(6, label, adminBadge()) : new HBox(6, label);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private VBox conversationCard(ConversationData conversation) {
        Label title = new Label("آگهی: " + conversation.adTitle());
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px;");
        HBox parties = new HBox(6, partyNode("خریدار", conversation.buyerUsername(), conversation.buyerAdmin()),
                new Label("|"), partyNode("فروشنده", conversation.sellerUsername(), conversation.sellerAdmin()));
        parties.setAlignment(Pos.CENTER_LEFT);
        Label preview = new Label(conversation.lastMessagePreview() == null ? "هنوز پیامی ارسال نشده" : conversation.lastMessagePreview());
        preview.setStyle("-fx-text-fill: #ddd;");

        Label timeLabel = new Label("");
        timeLabel.setStyle("-fx-text-fill: #777; -fx-font-size: 11px;");

        if (conversation.lastMessageAt() != null) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(conversation.lastMessageAt());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                timeLabel.setText(" - " + dateTime.format(formatter));
            } catch (Exception e) {
                System.err.println("خطا در پارس زمان آخرین پیام: " + e.getMessage());
            }
        }

        HBox bottomRow = new HBox(5);
        bottomRow.getChildren().addAll(preview, timeLabel);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(6, title, parties, bottomRow);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.06); -fx-padding: 14; -fx-background-radius: 10; -fx-cursor: hand;");
        card.setOnMouseClicked(event -> openConversation(conversation));
        return card;
    }

    private void openConversation(ConversationData conversation) {
        ConversationDetailScreen screen = new ConversationDetailScreen(conversation, this::loadConversations);
        Stage stage = new Stage();
        stage.setTitle("گفت‌وگو: " + conversation.adTitle());
        stage.setScene(new Scene(screen.getView()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    @FXML
    private void goBack() {
        if (viewManager != null) viewManager.toMain();
    }
}
