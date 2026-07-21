package divar.aut.frontend.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import divar.aut.frontend.model.CityData;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;

/**
 * Service class for city-related operations.
 * <p>
 * Provides methods to fetch cities from the backend REST API.
 * </p>
 */
public class CityService {

    /**
     * JSON serializer/deserializer for response bodies.
     */
    private static final Gson GSON = new Gson();

    /**
     * Retrieves a list of all cities.
     *
     * @param onSuccess callback accepting the list of {@link CityData}.
     * @param onError   callback accepting an error message.
     */
    public void listAll(Consumer<List<CityData>> onSuccess, Consumer<String> onError) {
        ApiClient.send("GET", "/api/cities", null,
                response -> {
                    if (response.statusCode() == 200) {
                        Type listType = new TypeToken<List<CityData>>() {
                        }.getType();
                        List<CityData> list = GSON.fromJson(response.body(), listType);
                        onSuccess.accept(list);
                    } else {
                        onError.accept(ApiClient.extractErrorMessage(response, "Error fetching cities: "));
                    }
                }, onError);
    }
}