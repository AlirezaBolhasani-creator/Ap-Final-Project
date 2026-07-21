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

/**
 * JavaFX controller for the list of user conversations.
 * Displays all conversations the current user is involved in, with
 * previews of the last message, timestamps, and unread count badges.
 * Allows opening a conversation detail view by clicking on a card.
 * Interacts with {@link ConversationService} to fetch conversations.
 */
public class ConversationsController {

    @FXML private VBox conversationList;
    @FXML private Label statusLabel;

    private final ConversationService conversationService = new ConversationService();
    private ViewManager viewManager;

    /**
     * Injects the view manager and triggers loading of conversations.
     *
     * @param viewManager the navigation manager for switching screens.
     */
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
            emptyLabel.getStyleClass().add("empty-state");
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
        icon.setIconColor(javafx.scene.paint.Color.web("#fbbf24"));
        Label badge = new Label("ادمین", icon);
        badge.getStyleClass().addAll("badge", "badge-warning", "admin-badge");
        return badge;
    }

    private Label unreadBadge(int count) {
        Label badge = new Label(count > 99 ? "99+" : String.valueOf(count));
        badge.getStyleClass().addAll("badge", "unread-badge");
        return badge;
    }

    private HBox partyNode(String role, String username, boolean isAdmin) {
        HBox box;
        if (isAdmin) {
            Label label = new Label(username);
            label.getStyleClass().add("party-admin");
            box = new HBox(6, label, adminBadge());
        } else {
            Label label = new Label(role + ": " + username);
            label.getStyleClass().add("party-name");
            box = new HBox(6, label);
        }
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private VBox conversationCard(ConversationData conversation) {
        Label title = new Label("آگهی: " + conversation.adTitle());
        title.getStyleClass().add("convo-title");

        HBox titleRow = new HBox(8, title);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        if (conversation.unreadCount() > 0) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            titleRow.getChildren().addAll(spacer, unreadBadge(conversation.unreadCount()));
        }

        HBox parties = new HBox(6, partyNode("خریدار", conversation.buyerUsername(), conversation.buyerAdmin()),
                new Label("|"), partyNode("فروشنده", conversation.sellerUsername(), conversation.sellerAdmin()));
        parties.setAlignment(Pos.CENTER_LEFT);
        Label preview = new Label(conversation.lastMessagePreview() == null ? "هنوز پیامی ارسال نشده" : conversation.lastMessagePreview());
        preview.getStyleClass().add(conversation.unreadCount() > 0 ? "convo-preview-unread" : "convo-preview");

        Label timeLabel = new Label("");
        timeLabel.getStyleClass().add("text-caption");

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

        VBox card = new VBox(6, titleRow, parties, bottomRow);
        card.getStyleClass().addAll("card", "card-hover", "convo-card");
        if (conversation.unreadCount() > 0) {
            card.getStyleClass().add("convo-card-unread");
        }
        card.setOnMouseClicked(event -> openConversation(conversation));
        return card;
    }

    private void openConversation(ConversationData conversation) {
        ConversationDetailScreen screen = new ConversationDetailScreen(conversation, this::loadConversations);
        divar.aut.frontend.ui.ThemeManager.applyCurrentMode(screen.getView());
        Stage stage = new Stage();
        stage.setTitle("گفت‌وگو: " + conversation.adTitle());
        javafx.scene.paint.Color bg = divar.aut.frontend.ui.ThemeManager.isLightMode()
                ? javafx.scene.paint.Color.web("#fffaf0") : javafx.scene.paint.Color.web("#0a1120");
        stage.setScene(new Scene(screen.getView(), bg));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    /**
     * Navigates back to the main application screen using the injected {@link ViewManager}.
     */
    @FXML
    private void goBack() {
        if (viewManager != null) viewManager.toMain();
    }
}