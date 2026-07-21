package divar.aut.frontend.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import divar.aut.frontend.model.AdData;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;

/**
 * Service class for managing user favorites (saved advertisements).
 * <p>
 * Provides methods to list, add, and remove favorites. Communicates with the
 * backend REST API via {@link ApiClient}.
 * </p>
 */
public class FavoriteService {

    /**
     * JSON serializer/deserializer for request/response bodies.
     */
    private static final Gson GSON = new Gson();

    /**
     * Retrieves the list of favorite advertisements for the current user.
     *
     * @param onSuccess callback accepting the list of {@link AdData}.
     * @param onError   callback accepting an error message.
     */
    public void listFavorites(Consumer<List<AdData>> onSuccess, Consumer<String> onError) {
        ApiClient.send("GET", "/api/favorites", null,
                response -> handleList(response, onSuccess, onError), onError);
    }

    /**
     * Adds an advertisement to the current user's favorites.
     *
     * @param adId      the ID of the ad to add.
     * @param onSuccess callback accepting a success message.
     * @param onError   callback accepting an error message.
     */
    public void addFavorite(Long adId, Consumer<String> onSuccess, Consumer<String> onError) {
        ApiClient.send("POST", "/api/favorites/" + adId, null,
                response -> {
                    if (response.statusCode() == 200 || response.statusCode() == 201) {
                        onSuccess.accept("آگهی به علاقه‌مندی‌ها اضافه شد");
                    } else {
                        onError.accept(ApiClient.extractErrorMessage(response, "Error adding favorite: "));
                    }
                }, onError);
    }

    /**
     * Removes an advertisement from the current user's favorites.
     *
     * @param adId      the ID of the ad to remove.
     * @param onSuccess callback accepting a success message.
     * @param onError   callback accepting an error message.
     */
    public void removeFavorite(Long adId, Consumer<String> onSuccess, Consumer<String> onError) {
        ApiClient.send("DELETE", "/api/favorites/" + adId, null,
                response -> {
                    if (response.statusCode() == 200 || response.statusCode() == 204) {
                        onSuccess.accept("آگهی از علاقه‌مندی‌ها حذف شد");
                    } else {
                        onError.accept(ApiClient.extractErrorMessage(response, "Error removing favorite: "));
                    }
                }, onError);
    }

    /**
     * Handles the HTTP response for a favorite list request.
     *
     * @param response  the HTTP response.
     * @param onSuccess callback accepting the list of ads.
     * @param onError   callback accepting an error message.
     */
    private void handleList(HttpResponse<String> response, Consumer<List<AdData>> onSuccess, Consumer<String> onError) {
        if (response.statusCode() == 200) {
            Type listType = new TypeToken<List<AdData>>() {
            }.getType();
            onSuccess.accept(GSON.fromJson(response.body(), listType));
        } else {
            onError.accept(ApiClient.extractErrorMessage(response, "Error fetching favorites: "));
        }
    }
}