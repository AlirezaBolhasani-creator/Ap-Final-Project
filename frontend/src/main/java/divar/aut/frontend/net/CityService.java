package divar.aut.frontend.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import divar.aut.frontend.model.CityData;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;

/** Talks to /api/cities/**. */
public class CityService {

    private static final Gson GSON = new Gson();

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
