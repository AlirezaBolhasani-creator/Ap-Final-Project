package divar.aut.frontend.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import divar.aut.frontend.model.RatingData;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;

/**
 * Service class for rating-related operations.
 * <p>
 * Provides methods to submit, delete, and list ratings for sellers.
 * Communicates with the backend REST API via {@link ApiClient}.
 * </p>
 */
public class RatingService {

    /**
     * JSON serializer/deserializer for request/response bodies.
     */
    private static final Gson GSON = new Gson();

    /**
     * Submits a rating for a seller based on an advertisement.
     *
     * @param adId      the ID of the advertisement context.
     * @param score     the rating score (1–5).
     * @param comment   an optional comment (may be null or empty).
     * @param onSuccess callback accepting the created {@link RatingData}.
     * @param onError   callback accepting an error message.
     */
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

    /**
     * Deletes a rating by its ID (admin action).
     *
     * @param ratingId  the ID of the rating to delete.
     * @param onSuccess callback to run on successful deletion.
     * @param onError   callback accepting an error message.
     */
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

    /**
     * Retrieves all ratings for a specific seller.
     *
     * @param sellerId  the ID of the seller.
     * @param onSuccess callback accepting the list of {@link RatingData}.
     * @param onError   callback accepting an error message.
     */
    public void listRatings(Long sellerId, Consumer<List<RatingData>> onSuccess, Consumer<String> onError) {
        ApiClient.send("GET", "/api/sellers/" + sellerId + "/ratings", null,
                response -> handleList(response, onSuccess, onError), onError);
    }

    /**
     * Handles the HTTP response for a rating list request.
     *
     * @param response  the HTTP response.
     * @param onSuccess callback accepting the list of ratings.
     * @param onError   callback accepting an error message.
     */
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

    /**
     * Internal record for the rating request body.
     *
     * @param score   the rating score.
     * @param comment the optional comment.
     */
    private record RatingRequestBody(int score, String comment) {
    }
}