package divar.aut.frontend.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import divar.aut.frontend.model.ConversationData;
import divar.aut.frontend.model.MessageData;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;

/**
 * Service class for conversation-related operations.
 * <p>
 * Provides methods to start conversations, list conversations, fetch messages,
 * and send messages. Communicates with the backend REST API via {@link ApiClient}.
 * </p>
 */
public class ConversationService {

    /**
     * JSON serializer/deserializer for request/response bodies.
     */
    private static final Gson GSON = new Gson();

    /**
     * Starts a new conversation for a given advertisement.
     *
     * @param adId      the ID of the advertisement.
     * @param onSuccess callback accepting the created {@link ConversationData}.
     * @param onError   callback accepting an error message.
     */
    public void startConversation(Long adId, Consumer<ConversationData> onSuccess, Consumer<String> onError) {
        ApiClient.send("POST", "/api/conversations?adId=" + adId, null,
                response -> {
                    if (response.statusCode() == 200 || response.statusCode() == 201) {
                        onSuccess.accept(GSON.fromJson(response.body(), ConversationData.class));
                    } else {
                        onError.accept(ApiClient.extractErrorMessage(response, "Error starting conversation: "));
                    }
                }, onError);
    }

    /**
     * Retrieves all conversations for the current user.
     *
     * @param onSuccess callback accepting the list of {@link ConversationData}.
     * @param onError   callback accepting an error message.
     */
    public void listConversations(Consumer<List<ConversationData>> onSuccess, Consumer<String> onError) {
        ApiClient.send("GET", "/api/conversations", null,
                response -> handleConversationList(response, onSuccess, onError), onError);
    }

    /**
     * Retrieves all messages for a specific conversation.
     *
     * @param conversationId the ID of the conversation.
     * @param onSuccess      callback accepting the list of {@link MessageData}.
     * @param onError        callback accepting an error message.
     */
    public void listMessages(Long conversationId, Consumer<List<MessageData>> onSuccess, Consumer<String> onError) {
        ApiClient.send("GET", "/api/conversations/" + conversationId + "/messages", null,
                response -> handleMessageList(response, onSuccess, onError), onError);
    }

    /**
     * Sends a new message in a conversation.
     *
     * @param conversationId the ID of the conversation.
     * @param content        the message content.
     * @param onSuccess      callback accepting the sent {@link MessageData}.
     * @param onError        callback accepting an error message.
     */
    public void sendMessage(Long conversationId, String content, Consumer<MessageData> onSuccess, Consumer<String> onError) {
        String body = GSON.toJson(new MessageRequestBody(content));
        ApiClient.send("POST", "/api/conversations/" + conversationId + "/messages", body,
                response -> {
                    if (response.statusCode() == 200 || response.statusCode() == 201) {
                        onSuccess.accept(GSON.fromJson(response.body(), MessageData.class));
                    } else {
                        onError.accept(ApiClient.extractErrorMessage(response, "Error sending message: "));
                    }
                }, onError);
    }

    /**
     * Handles the HTTP response for a conversation list request.
     *
     * @param response  the HTTP response.
     * @param onSuccess callback accepting the list of conversations.
     * @param onError   callback accepting an error message.
     */
    private void handleConversationList(HttpResponse<String> response, Consumer<List<ConversationData>> onSuccess,
                                        Consumer<String> onError) {
        if (response.statusCode() == 200) {
            Type listType = new TypeToken<List<ConversationData>>() {
            }.getType();
            onSuccess.accept(GSON.fromJson(response.body(), listType));
        } else {
            onError.accept(ApiClient.extractErrorMessage(response, "Error fetching conversations: "));
        }
    }

    /**
     * Handles the HTTP response for a message list request.
     *
     * @param response  the HTTP response.
     * @param onSuccess callback accepting the list of messages.
     * @param onError   callback accepting an error message.
     */
    private void handleMessageList(HttpResponse<String> response, Consumer<List<MessageData>> onSuccess,
                                   Consumer<String> onError) {
        if (response.statusCode() == 200) {
            Type listType = new TypeToken<List<MessageData>>() {
            }.getType();
            onSuccess.accept(GSON.fromJson(response.body(), listType));
        } else {
            onError.accept(ApiClient.extractErrorMessage(response, "Error fetching messages: "));
        }
    }

    /**
     * Internal record for the message request body.
     *
     * @param content the message content.
     */
    private record MessageRequestBody(String content) {
    }
}