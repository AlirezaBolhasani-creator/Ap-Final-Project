package divar.aut.frontend.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import divar.aut.frontend.model.AdData;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;

public class FavoriteService {

    private static final Gson GSON = new Gson();

    public void listFavorites(Consumer<List<AdData>> onSuccess, Consumer<String> onError) {
        ApiClient.send("GET", "/api/favorites", null,
                response -> handleList(response, onSuccess, onError), onError);
    }

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
