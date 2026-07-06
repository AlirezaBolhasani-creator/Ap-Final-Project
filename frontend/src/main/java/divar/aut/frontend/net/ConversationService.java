package divar.aut.frontend.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import divar.aut.frontend.model.ConversationData;
import divar.aut.frontend.model.MessageData;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;

public class ConversationService {

    private static final Gson GSON = new Gson();

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

    public void listConversations(Consumer<List<ConversationData>> onSuccess, Consumer<String> onError) {
        ApiClient.send("GET", "/api/conversations", null,
                response -> handleConversationList(response, onSuccess, onError), onError);
    }

    public void listMessages(Long conversationId, Consumer<List<MessageData>> onSuccess, Consumer<String> onError) {
        ApiClient.send("GET", "/api/conversations/" + conversationId + "/messages", null,
                response -> handleMessageList(response, onSuccess, onError), onError);
    }

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

    private record MessageRequestBody(String content) {
    }
}
