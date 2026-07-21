package divar.aut.frontend.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import divar.aut.frontend.model.*;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;

/**
 * Service class for administrative operations.
 * <p>
 * Provides methods to manage ads, users, categories, and cities.
 * Communicates with the backend REST API via {@link ApiClient}.
 * All methods accept success and error callbacks.
 * </p>
 */
public class AdminService {

    /**
     * JSON serializer/deserializer for request/response bodies.
     */
    private static final Gson GSON = new Gson();

    /**
     * Retrieves a list of all advertisements (admin view).
     *
     * @param ok    callback accepting the list of {@link AdData}.
     * @param error callback accepting an error message.
     */
    public void listAds(Consumer<List<AdData>> ok, Consumer<String> error) {
        getList("/api/admin/ads", AdData.class, ok, error);
    }

    /**
     * Retrieves a list of all users.
     *
     * @param ok    callback accepting the list of {@link UserData}.
     * @param error callback accepting an error message.
     */
    public void listUsers(Consumer<List<UserData>> ok, Consumer<String> error) {
        getList("/api/admin/users", UserData.class, ok, error);
    }

    /**
     * Retrieves system statistics for the admin dashboard.
     *
     * @param ok    callback accepting {@link AdminStatsData}.
     * @param error callback accepting an error message.
     */
    public void getStats(Consumer<AdminStatsData> ok, Consumer<String> error) {
        ApiClient.send("GET", "/api/admin/stats", null, r -> parse(r, AdminStatsData.class, ok, error), error);
    }

    /**
     * Deletes an advertisement by its ID (admin action).
     *
     * @param id    the ID of the ad to delete.
     * @param ok    callback accepting a success message.
     * @param error callback accepting an error message.
     */
    public void deleteAd(Long id, Consumer<String> ok, Consumer<String> error) {
        action("DELETE", "/api/admin/ads/" + id, null, ok, error);
    }

    /**
     * Blocks or unblocks a user.
     *
     * @param id      the ID of the user.
     * @param blocked true to block, false to unblock.
     * @param ok      callback accepting a success message.
     * @param error   callback accepting an error message.
     */
    public void setUserBlocked(Long id, boolean blocked, Consumer<String> ok, Consumer<String> error) {
        action("PUT", "/api/admin/users/" + id + (blocked ? "/block" : "/unblock"), null, ok, error);
    }

    /**
     * Creates a new category.
     *
     * @param name  the name of the category.
     * @param ok    callback accepting the created {@link CategoryData}.
     * @param error callback accepting an error message.
     */
    public void createCategory(String name, Consumer<CategoryData> ok, Consumer<String> error) {
        jsonAction("POST", "/api/admin/categories", name, CategoryData.class, ok, error);
    }

    /**
     * Updates an existing category.
     *
     * @param id    the ID of the category to update.
     * @param name  the new name.
     * @param ok    callback accepting the updated {@link CategoryData}.
     * @param error callback accepting an error message.
     */
    public void updateCategory(Long id, String name, Consumer<CategoryData> ok, Consumer<String> error) {
        jsonAction("PUT", "/api/admin/categories/" + id, name, CategoryData.class, ok, error);
    }

    /**
     * Retrieves the number of ads associated with a category.
     *
     * @param id    the category ID.
     * @param ok    callback accepting {@link MetadataUsageData}.
     * @param error callback accepting an error message.
     */
    public void getCategoryUsage(Long id, Consumer<MetadataUsageData> ok, Consumer<String> error) {
        ApiClient.send("GET", "/api/admin/categories/" + id + "/usage", null,
                response -> parse(response, MetadataUsageData.class, ok, error), error);
    }

    /**
     * Deletes a category with a specified strategy.
     *
     * @param id            the category ID.
     * @param strategy      deletion strategy ("REASSIGN" or "DELETE_ADS").
     * @param replacementId the replacement category ID (required for REASSIGN).
     * @param ok            callback accepting a success message.
     * @param error         callback accepting an error message.
     */
    public void deleteCategory(Long id, String strategy, Long replacementId, Consumer<String> ok, Consumer<String> error) {
        action("DELETE", "/api/admin/categories/" + id,
                GSON.toJson(new MetadataDeleteBody(strategy, replacementId)), ok, error);
    }

    /**
     * Creates a new city.
     *
     * @param name  the name of the city.
     * @param ok    callback accepting the created {@link CityData}.
     * @param error callback accepting an error message.
     */
    public void createCity(String name, Consumer<CityData> ok, Consumer<String> error) {
        jsonAction("POST", "/api/admin/cities", name, CityData.class, ok, error);
    }

    /**
     * Updates an existing city.
     *
     * @param id    the ID of the city to update.
     * @param name  the new name.
     * @param ok    callback accepting the updated {@link CityData}.
     * @param error callback accepting an error message.
     */
    public void updateCity(Long id, String name, Consumer<CityData> ok, Consumer<String> error) {
        jsonAction("PUT", "/api/admin/cities/" + id, name, CityData.class, ok, error);
    }

    /**
     * Retrieves the number of ads associated with a city.
     *
     * @param id    the city ID.
     * @param ok    callback accepting {@link MetadataUsageData}.
     * @param error callback accepting an error message.
     */
    public void getCityUsage(Long id, Consumer<MetadataUsageData> ok, Consumer<String> error) {
        ApiClient.send("GET", "/api/admin/cities/" + id + "/usage", null,
                response -> parse(response, MetadataUsageData.class, ok, error), error);
    }

    /**
     * Deletes a city with a specified strategy.
     *
     * @param id            the city ID.
     * @param strategy      deletion strategy ("REASSIGN" or "DELETE_ADS").
     * @param replacementId the replacement city ID (required for REASSIGN).
     * @param ok            callback accepting a success message.
     * @param error         callback accepting an error message.
     */
    public void deleteCity(Long id, String strategy, Long replacementId, Consumer<String> ok, Consumer<String> error) {
        action("DELETE", "/api/admin/cities/" + id,
                GSON.toJson(new MetadataDeleteBody(strategy, replacementId)), ok, error);
    }

    /**
     * Generic method to fetch a list of resources.
     *
     * @param path  the API endpoint.
     * @param type  the class of each element in the list.
     * @param ok    callback accepting the list.
     * @param error callback accepting an error message.
     * @param <T>   the type of the list elements.
     */
    private <T> void getList(String path, Class<T> type, Consumer<List<T>> ok, Consumer<String> error) {
        ApiClient.send("GET", path, null, response -> {
            if (response.statusCode() == 200) {
                Type listType = TypeToken.getParameterized(List.class, type).getType();
                ok.accept(GSON.fromJson(response.body(), listType));
            } else error.accept(ApiClient.extractErrorMessage(response, "Admin request failed: "));
        }, error);
    }

    /**
     * Generic method for actions that send a simple name in the request body.
     *
     * @param method  the HTTP method (POST, PUT).
     * @param path    the API endpoint.
     * @param name    the name to send.
     * @param type    the response type class.
     * @param ok      callback accepting the parsed response.
     * @param error   callback accepting an error message.
     * @param <T>     the response type.
     */
    private <T> void jsonAction(String method, String path, String name, Class<T> type, Consumer<T> ok, Consumer<String> error) {
        ApiClient.send(method, path, GSON.toJson(new NameBody(name)), r -> parse(r, type, ok, error), error);
    }

    /**
     * Helper to parse an HTTP response into a domain object.
     *
     * @param response the HTTP response.
     * @param type     the target class.
     * @param ok       success callback.
     * @param error    error callback.
     * @param <T>      the type to parse.
     */
    private <T> void parse(HttpResponse<String> response, Class<T> type, Consumer<T> ok, Consumer<String> error) {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            ok.accept(GSON.fromJson(response.body(), type));
        } else {
            error.accept(ApiClient.extractErrorMessage(response, "Admin request failed: "));
        }
    }

    /**
     * Generic method for actions that return a simple success message.
     *
     * @param method the HTTP method.
     * @param path   the API endpoint.
     * @param body   the request body (may be null).
     * @param ok     callback accepting a success message.
     * @param error  callback accepting an error message.
     */
    private void action(String method, String path, String body, Consumer<String> ok, Consumer<String> error) {
        ApiClient.send(method, path, body, response -> {
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                ok.accept("عملیات با موفقیت انجام شد");
            } else {
                error.accept(ApiClient.extractErrorMessage(response, "Admin request failed: "));
            }
        }, error);
    }

    /**
     * Internal record for JSON body containing a name.
     */
    private record NameBody(String name) {}

    /**
     * Internal record for metadata deletion request body.
     */
    private record MetadataDeleteBody(String strategy, Long replacementId) {}
}