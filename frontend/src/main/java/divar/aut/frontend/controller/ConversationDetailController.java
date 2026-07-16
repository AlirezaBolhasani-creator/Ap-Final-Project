package divar.aut.frontend.controller;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
                dateLabel.setStyle("-fx-background-color: rgba(255,255,255,0.15); " +
                        "-fx-text-fill: #ddd; -fx-padding: 5 12; " +
                        "-fx-background-radius: 12; -fx-font-size: 11px;");
                HBox dateBox = new HBox(dateLabel);
                dateBox.setAlignment(Pos.CENTER);
                dateBox.setStyle("-fx-padding: 10 0 10 0;");
                messagesBox.getChildren().add(dateBox);

                lastDate = messageDate;
            }

            Label textLabel = new Label(message.senderUsername() + ": " + message.content());
            textLabel.setWrapText(true);
            textLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

            String timeText = (dateTime != null) ? dateTime.format(timeFormatter) : "";
            Label timeLabel = new Label(timeText);
            timeLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 10px;");

            VBox bubbleContent = new VBox(5, textLabel, timeLabel);

            bubbleContent.setAlignment(Pos.BOTTOM_RIGHT);
            bubbleContent.setMaxWidth(480);
            bubbleContent.setStyle("-fx-background-color: rgba(255,255,255,0.08); " +
                    "-fx-padding: 8 12; -fx-background-radius: 8;");

            messagesBox.getChildren().add(bubbleContent);
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
                    if (error.contains("403") || error.toLowerCase().contains("forbidden")) {
                        messageField.setDisable(true); // برای همیشه دیزیبل می‌ماند
                        messageField.setPromptText("شما امکان ارسال پیام در این گفتگو را ندارید.");
                        messageField.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #333;");
                        statusLabel.setText("امکان ارسال پیام وجود ندارد (مسدود).");
                    } else {
                        messageField.setDisable(false);
                        statusLabel.setText("خطا در ارسال پیام: " + error);
                    }
                }
        );
    }
}
