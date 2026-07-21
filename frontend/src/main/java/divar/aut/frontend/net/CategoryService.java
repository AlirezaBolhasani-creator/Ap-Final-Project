package divar.aut.frontend.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import divar.aut.frontend.model.CategoryData;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;

/**
 * Service class for category-related operations.
 * <p>
 * Provides methods to fetch categories from the backend REST API.
 * </p>
 */
public class CategoryService {

    /**
     * JSON serializer/deserializer for response bodies.
     */
    private static final Gson GSON = new Gson();

    /**
     * Retrieves a list of all categories.
     *
     * @param onSuccess callback accepting the list of {@link CategoryData}.
     * @param onError   callback accepting an error message.
     */
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