package divar.aut.frontend.controller;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import divar.aut.frontend.model.AdDetailData;
import divar.aut.frontend.model.ConversationData;
import divar.aut.frontend.model.MessageData;
import divar.aut.frontend.net.AdService;
import divar.aut.frontend.net.ConversationService;
import divar.aut.frontend.ui.ConversationDetailScreen;
import divar.aut.frontend.ui.ViewManager;
import divar.aut.frontend.controller.AdDetailsController;
import divar.aut.frontend.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.application.Platform;
import org.kordamp.ikonli.javafx.FontIcon;
import java.io.IOException;

import java.util.List;

/**
 * JavaFX controller for displaying a conversation detail view.
 * Shows the full message history between buyer and seller, with date
 * separators, sender labels (with admin badge for admin senders), and
 * a text field for sending new messages. Uses {@link ConversationService}
 * to fetch messages and send new ones.
 */
public class ConversationDetailController {

    @FXML private Label headerLabel;
    @FXML private ScrollPane messageScrollPane;
    @FXML private VBox messagesBox;
    @FXML private TextField messageField;
    @FXML private Label statusLabel;

    private final ConversationService conversationService = new ConversationService();
    private final AdService adService = new AdService();
    private ViewManager viewManager;
    private Long adId;
    private String adTitle;
    private Runnable returnToAdAction;

    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    @FXML
    private void initialize() {
        messagesBox.heightProperty().addListener((observable, oldValue, newValue) -> scrollToBottom());
    }

    @FXML
    private void handleBack() {
        if (viewManager != null) {
            viewManager.toConversations();
        }
    }

    @FXML
    private void handleBackToAd() {
        if (viewManager == null) {
            return;
        }
        Long targetAdId = conversation != null ? conversation.adId() : adId;
        if (targetAdId == null) {
            return;
        }
        adService.fetchAdDetails(targetAdId, adDetail -> Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdDetails.fxml"));
                Parent view = loader.load();
                AdDetailsController controller = loader.getController();
                Runnable backAction = returnToAdAction;
                if (backAction == null) {
                    backAction = () -> {
                        try {
                            if (viewManager == null) return;
                            Parent conversationView;
                            if (conversation != null) {
                                ConversationDetailScreen screen = new ConversationDetailScreen(viewManager, conversation, onMessageSent);
                                conversationView = screen.getView();
                            } else {
                                ConversationDetailScreen screen = new ConversationDetailScreen(viewManager, adId, adTitle, onMessageSent);
                                conversationView = screen.getView();
                            }
                            viewManager.show(conversationView);
                        } catch (Exception e) {
                            Platform.runLater(() -> statusLabel.setText("خطا در بازگشت به گفت‌وگو"));
                        }
                    };
                }
                controller.setData(adDetail, new AdService(), viewManager.getUserRole(), false, null, viewManager, null, backAction);
                viewManager.show(view);
            } catch (IOException e) {
                e.printStackTrace();
                statusLabel.setText("خطا در باز کردن آگهی مرتبط");
            }
        }), error -> Platform.runLater(() -> statusLabel.setText("خطا در دریافت آگهی: " + error)));
    }

    private ConversationData conversation;
    private Runnable onMessageSent;

    /**
     * Initialises the controller with the conversation data.
     * Sets the header label and loads the message history.
     *
     * @param conversation the conversation data to display.
     * @param onMessageSent optional callback to run after a message is sent successfully.
     */
    public void setData(ConversationData conversation, Runnable onMessageSent, Runnable returnToAdAction) {
        this.conversation = conversation;
        this.adId = conversation.adId();
        this.adTitle = conversation.adTitle();
        this.onMessageSent = onMessageSent;
        this.returnToAdAction = returnToAdAction;
        headerLabel.setText("گفت‌وگو درباره: " + conversation.adTitle());
        loadMessages();
    }

    public void setData(Long adId, String adTitle, Runnable onMessageSent, Runnable returnToAdAction) {
        this.conversation = null;
        this.adId = adId;
        this.adTitle = adTitle;
        this.onMessageSent = onMessageSent;
        this.returnToAdAction = returnToAdAction;
        headerLabel.setText("گفت‌وگو درباره: " + adTitle);
        messagesBox.getChildren().clear();
        Label emptyLabel = new Label("هنوز پیامی ارسال نشده است.");
        emptyLabel.getStyleClass().add("empty-state");
        messagesBox.getChildren().add(emptyLabel);
    }

    private void loadMessages() {
        if (conversation == null) {
            messagesBox.getChildren().clear();
            Label emptyLabel = new Label("هنوز پیامی ارسال نشده است.");
            emptyLabel.getStyleClass().add("empty-state");
            messagesBox.getChildren().add(emptyLabel);
            return;
        }
        conversationService.listMessages(conversation.id(), this::renderMessages,
                error -> statusLabel.setText("خطا در دریافت پیام‌ها: " + error));
    }

    private void showError(String error) {
        statusLabel.setText(error);
    }

    private void renderMessages(List<MessageData> messages) {
        messages.sort(Comparator.comparing(message -> {
            if (message.sentAt() == null) return LocalDateTime.MIN;
            try {
                return LocalDateTime.parse(message.sentAt());
            } catch (Exception e) {
                return LocalDateTime.MIN;
            }
        }));
        messagesBox.getChildren().clear();
        if (messages.isEmpty()) {
            Label emptyLabel = new Label("هنوز پیامی ارسال نشده است.");
            emptyLabel.getStyleClass().add("empty-state");
            messagesBox.getChildren().add(emptyLabel);
            scrollToBottom();
            return;
        }

        LocalDate lastDate = null;
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        for (MessageData message : messages) {
            LocalDateTime dateTime = null;
            LocalDate messageDate = null;

            try {
                if (message.sentAt() != null) {
                    dateTime = LocalDateTime.parse(message.sentAt());
                    messageDate = dateTime.toLocalDate();
                }
            } catch (DateTimeParseException e) {
                System.err.println("خطا در پارس کردن زمان پیام: " + message.sentAt());
            }

            if (messageDate != null && (lastDate == null || !lastDate.equals(messageDate))) {
                Label dateLabel = new Label(messageDate.format(dateFormatter));
                dateLabel.getStyleClass().add("chat-date");
                HBox dateBox = new HBox(dateLabel);
                dateBox.setAlignment(Pos.CENTER);
                dateBox.getStyleClass().add("chat-date-box");
                messagesBox.getChildren().add(dateBox);

                lastDate = messageDate;
            }

            boolean admin = message.senderAdmin();
            Label senderLabel = new Label(admin ? "ادمین" : message.senderUsername());
            senderLabel.getStyleClass().add("chat-sender");
            HBox senderRow;
            if (admin) {
                FontIcon icon = new FontIcon("fas-shield-alt");
                icon.setIconSize(9);
                icon.setIconColor(javafx.scene.paint.Color.web("#fbbf24"));
                Label badge = new Label("ادمین", icon);
                badge.getStyleClass().addAll("badge", "badge-warning", "admin-badge");
                senderLabel.getStyleClass().add("chat-sender-admin");
                senderRow = new HBox(6, senderLabel, badge);
            } else {
                senderRow = new HBox(6, senderLabel);
            }
            senderRow.setAlignment(Pos.CENTER_LEFT);

            Label textLabel = new Label(message.content());
            textLabel.setWrapText(true);
            textLabel.getStyleClass().add("chat-text");

            String timeText = (dateTime != null) ? dateTime.format(timeFormatter) : "";
            Label timeLabel = new Label(timeText);
            timeLabel.getStyleClass().add("chat-time");

            VBox bubbleContent = new VBox(5, senderRow, textLabel, timeLabel);
            bubbleContent.setAlignment(Pos.BOTTOM_RIGHT);
            bubbleContent.setMaxWidth(480);
            bubbleContent.getStyleClass().add("chat-bubble");

            boolean isSelf = SessionManager.getInstance().getUsername() != null &&
                    SessionManager.getInstance().getUsername().equals(message.senderUsername());
            HBox bubbleWrapper = new HBox(bubbleContent);
            bubbleWrapper.setAlignment(isSelf ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
            bubbleWrapper.getStyleClass().addAll("chat-bubble-container", isSelf ? "self" : "other");
            bubbleWrapper.setMaxWidth(Double.MAX_VALUE);

            messagesBox.getChildren().add(bubbleWrapper);
        }
        scrollToBottom();
    }

    private void scrollToBottom() {
        if (messageScrollPane != null) {
            Platform.runLater(() -> {
                messageScrollPane.layout();
                Platform.runLater(() -> messageScrollPane.setVvalue(messageScrollPane.getVmax()));
            });
        }
    }

    /**
     * Sends a new message in the current conversation.
     * Reads the content from {@code messageField}, clears it on success,
     * reloads the message list, and triggers the optional callback.
     * Disables the input field temporarily and, on a 403/forbidden error,
     * permanently disables it with a prompt message.
     */
    @FXML
    private void sendMessage() {
        String content = messageField.getText() == null ? "" : messageField.getText().trim();
        if (content.isEmpty()) return;
        messageField.setDisable(true);

        if (conversation == null) {
            if (adId == null) {
                statusLabel.setText("شناسه آگهی نامشخص است.");
                messageField.setDisable(false);
                return;
            }
            conversationService.startConversation(adId, startedConversation -> Platform.runLater(() -> {
                this.conversation = startedConversation;
                this.adId = startedConversation.adId();
                this.adTitle = startedConversation.adTitle();
                sendMessageToConversation(content);
            }), error -> Platform.runLater(() -> {
                showError(error);
                messageField.setDisable(false);
            }));
        } else {
            sendMessageToConversation(content);
        }
    }

    private void sendMessageToConversation(String content) {
        conversationService.sendMessage(conversation.id(), content,
                message -> Platform.runLater(() -> {
                    messageField.clear();
                    messageField.setDisable(false);
                    statusLabel.setText("");
                    loadMessages();
                    if (onMessageSent != null) onMessageSent.run();
                }),
                error -> Platform.runLater(() -> {
                    if (error.contains("403") || error.toLowerCase().contains("forbidden")) {
                        messageField.setDisable(true); // برای همیشه دیزیبل می‌ماند
                        messageField.setPromptText("شما امکان ارسال پیام در این گفتگو را ندارید.");
                        messageField.getStyleClass().add("chat-input-disabled");
                        statusLabel.setText("امکان ارسال پیام وجود ندارد (مسدود).");
                    } else {
                        messageField.setDisable(false);
                        statusLabel.setText("خطا در ارسال پیام: " + error);
                    }
                }));
    }
}