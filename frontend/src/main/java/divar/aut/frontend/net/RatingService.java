package divar.aut.frontend.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import divar.aut.frontend.model.RatingData;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;

public class RatingService {

    private static final Gson GSON = new Gson();

    public void submitRating(Long adId, int score, String comment,
                             Consumer<RatingData> onSuccess, Consumer<String> onError) {
        String body = GSON.toJson(new RatingRequestBody(score, comment));
        ApiClient.send("POST", "/ads/" + adId + "/ratings", body,
                response -> {
                    if (response.statusCode() == 200 || response.statusCode() == 201) {
                        onSuccess.accept(GSON.fromJson(response.body(), RatingData.class));
                    } else {
                        onError.accept(ApiClient.extractErrorMessage(response, "Error submitting rating: "));
                    }
                }, onError);
    }

    public void deleteRating(Long ratingId, Runnable onSuccess, Consumer<String> onError) {
        ApiClient.send("DELETE", "/ratings/" + ratingId, null,
                response -> {
                    if (response.statusCode() == 200 || response.statusCode() == 204) {
                        onSuccess.run();
                    } else {
                        onError.accept(ApiClient.extractErrorMessage(response, "Error deleting comment: "));
                    }
                }, onError);
    }

    public void listRatings(Long sellerId, Consumer<List<RatingData>> onSuccess, Consumer<String> onError) {
        ApiClient.send("GET", "/api/sellers/" + sellerId + "/ratings", null,
                response -> handleList(response, onSuccess, onError), onError);
    }

    private void handleList(HttpResponse<String> response, Consumer<List<RatingData>> onSuccess,
                            Consumer<String> onError) {
        if (response.statusCode() == 200) {
            Type listType = new TypeToken<List<RatingData>>() {
            }.getType();
            onSuccess.accept(GSON.fromJson(response.body(), listType));
        } else {
            onError.accept(ApiClient.extractErrorMessage(response, "Error fetching ratings: "));
        }
    }

    private record RatingRequestBody(int score, String comment) {
    }
}
