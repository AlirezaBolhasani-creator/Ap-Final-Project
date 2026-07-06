package divar.aut.frontend.controller;

import divar.aut.frontend.model.ConversationData;
import divar.aut.frontend.model.MessageData;
import divar.aut.frontend.net.ConversationService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;

public class ConversationDetailController {

    @FXML private Label headerLabel;
    @FXML private VBox messagesBox;
    @FXML private TextField messageField;
    @FXML private Label statusLabel;

    private final ConversationService conversationService = new ConversationService();
    private ConversationData conversation;
    private Runnable onMessageSent;

    public void setData(ConversationData conversation, Runnable onMessageSent) {
        this.conversation = conversation;
        this.onMessageSent = onMessageSent;
        headerLabel.setText("گفت‌وگو درباره: " + conversation.adTitle());
        loadMessages();
    }

    private void loadMessages() {
        conversationService.listMessages(conversation.id(), this::renderMessages,
                error -> statusLabel.setText("خطا در دریافت پیام‌ها: " + error));
    }

    private void renderMessages(List<MessageData> messages) {
        messagesBox.getChildren().clear();
        if (messages.isEmpty()) {
            Label emptyLabel = new Label("هنوز پیامی ارسال نشده است.");
            emptyLabel.setStyle("-fx-text-fill: #888;");
            messagesBox.getChildren().add(emptyLabel);
            return;
        }
        for (MessageData message : messages) {
            Label bubble = new Label(message.senderUsername() + ": " + message.content());
            bubble.setWrapText(true);
            bubble.setMaxWidth(480);
            bubble.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 8;");
            messagesBox.getChildren().add(bubble);
        }
    }

    @FXML
    private void sendMessage() {
        String content = messageField.getText() == null ? "" : messageField.getText().trim();
        if (content.isEmpty()) return;
        messageField.setDisable(true);
        conversationService.sendMessage(conversation.id(), content,
                message -> {
                    messageField.clear();
                    messageField.setDisable(false);
                    statusLabel.setText("");
                    loadMessages();
                    if (onMessageSent != null) onMessageSent.run();
                },
                error -> {
                    messageField.setDisable(false);
                    statusLabel.setText("خطا در ارسال پیام: " + error);
                }
        );
    }
}
