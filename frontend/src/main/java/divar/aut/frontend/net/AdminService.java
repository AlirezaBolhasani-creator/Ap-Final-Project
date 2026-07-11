package divar.aut.frontend.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import divar.aut.frontend.model.*;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;

public class AdminService {
    private static final Gson GSON = new Gson();

    public void listAds(Consumer<List<AdData>> ok, Consumer<String> error) { getList("/api/admin/ads", AdData.class, ok, error); }
    public void listUsers(Consumer<List<UserData>> ok, Consumer<String> error) { getList("/api/admin/users", UserData.class, ok, error); }
    public void getStats(Consumer<AdminStatsData> ok, Consumer<String> error) {
        ApiClient.send("GET", "/api/admin/stats", null, r -> parse(r, AdminStatsData.class, ok, error), error);
    }
    public void deleteAd(Long id, Consumer<String> ok, Consumer<String> error) { action("DELETE", "/api/admin/ads/" + id, null, ok, error); }
    public void setUserBlocked(Long id, boolean blocked, Consumer<String> ok, Consumer<String> error) {
        action("PUT", "/api/admin/users/" + id + (blocked ? "/block" : "/unblock"), null, ok, error);
    }
    public void createCategory(String name, Consumer<CategoryData> ok, Consumer<String> error) { jsonAction("POST", "/api/admin/categories", name, CategoryData.class, ok, error); }
    public void updateCategory(Long id, String name, Consumer<CategoryData> ok, Consumer<String> error) { jsonAction("PUT", "/api/admin/categories/" + id, name, CategoryData.class, ok, error); }
    public void getCategoryUsage(Long id, Consumer<MetadataUsageData> ok, Consumer<String> error) {
        ApiClient.send("GET", "/api/admin/categories/" + id + "/usage", null,
                response -> parse(response, MetadataUsageData.class, ok, error), error);
    }
    public void deleteCategory(Long id, String strategy, Long replacementId, Consumer<String> ok, Consumer<String> error) {
        action("DELETE", "/api/admin/categories/" + id,
                GSON.toJson(new MetadataDeleteBody(strategy, replacementId)), ok, error);
    }
    public void createCity(String name, Consumer<CityData> ok, Consumer<String> error) { jsonAction("POST", "/api/admin/cities", name, CityData.class, ok, error); }
    public void updateCity(Long id, String name, Consumer<CityData> ok, Consumer<String> error) { jsonAction("PUT", "/api/admin/cities/" + id, name, CityData.class, ok, error); }
    public void getCityUsage(Long id, Consumer<MetadataUsageData> ok, Consumer<String> error) {
        ApiClient.send("GET", "/api/admin/cities/" + id + "/usage", null,
                response -> parse(response, MetadataUsageData.class, ok, error), error);
    }
    public void deleteCity(Long id, String strategy, Long replacementId, Consumer<String> ok, Consumer<String> error) {
        action("DELETE", "/api/admin/cities/" + id,
                GSON.toJson(new MetadataDeleteBody(strategy, replacementId)), ok, error);
    }

    private <T> void getList(String path, Class<T> type, Consumer<List<T>> ok, Consumer<String> error) {
        ApiClient.send("GET", path, null, response -> {
            if (response.statusCode() == 200) {
                Type listType = TypeToken.getParameterized(List.class, type).getType();
                ok.accept(GSON.fromJson(response.body(), listType));
            } else error.accept(ApiClient.extractErrorMessage(response, "Admin request failed: "));
        }, error);
    }

    private <T> void jsonAction(String method, String path, String name, Class<T> type, Consumer<T> ok, Consumer<String> error) {
        ApiClient.send(method, path, GSON.toJson(new NameBody(name)), r -> parse(r, type, ok, error), error);
    }

    private <T> void parse(HttpResponse<String> response, Class<T> type, Consumer<T> ok, Consumer<String> error) {
        if (response.statusCode() >= 200 && response.statusCode() < 300) ok.accept(GSON.fromJson(response.body(), type));
        else error.accept(ApiClient.extractErrorMessage(response, "Admin request failed: "));
    }

    private void action(String method, String path, String body, Consumer<String> ok, Consumer<String> error) {
        ApiClient.send(method, path, body, response -> {
            if (response.statusCode() >= 200 && response.statusCode() < 300) ok.accept("عملیات با موفقیت انجام شد");
            else error.accept(ApiClient.extractErrorMessage(response, "Admin request failed: "));
        }, error);
    }

    private record NameBody(String name) {}
    private record MetadataDeleteBody(String strategy, Long replacementId) {}
}
