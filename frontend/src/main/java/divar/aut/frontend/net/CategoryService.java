package divar.aut.frontend.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import divar.aut.frontend.model.CategoryData;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;

/** Talks to /api/categories/**. */
public class CategoryService {

    private static final Gson GSON = new Gson();

    public void listAll(Consumer<List<CategoryData>> onSuccess, Consumer<String> onError) {
        ApiClient.send("GET", "/api/categories", null,
                response -> {
                    if (response.statusCode() == 200) {
                        Type listType = new TypeToken<List<CategoryData>>() {
                        }.getType();
                        List<CategoryData> list = GSON.fromJson(response.body(), listType);
                        onSuccess.accept(list);
                    } else {
                        onError.accept(ApiClient.extractErrorMessage(response, "Error fetching categories: "));
                    }
                }, onError);
    }
}
