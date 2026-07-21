package divar.aut.frontend.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import divar.aut.frontend.model.AdData;
import divar.aut.frontend.model.AdDetailData;
import divar.aut.frontend.model.AdRequestData;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

/**
 * Service class for advertisement-related operations.
 * <p>
 * Provides methods to fetch, create, update, delete, and manage advertisements,
 * including image uploads and status changes. Communicates with the backend
 * REST API via {@link ApiClient}.
 * </p>
 */
public class AdService {

    /**
     * JSON serializer/deserializer for request/response bodies.
     */
    private static final Gson GSON = new Gson();

    /**
     * Fetches a paginated list of all advertisements.
     *
     * @param page     the page number (0‑based).
     * @param onSuccess callback accepting the list of {@link AdData}.
     * @param onError   callback accepting an error message.
     */
    public void fetchAds(int page, Consumer<List<AdData>> onSuccess, Consumer<String> onError) {
        ApiClient.send("GET", "/ads?page=" + page, null,
                response -> handleList(response, onSuccess, onError), onError);
    }

    /**
     * Searches advertisements with optional filters and sorting.
     *
     * @param keyword    search keyword (may be null or empty).
     * @param categoryId category filter (may be null).
     * @param cityId     city filter (may be null).
     * @param minPrice   minimum price (may be null).
     * @param maxPrice   maximum price (may be null).
     * @param condition  item condition (e.g., "NEW", "USED"; may be null).
     * @param sortBy     sort criteria (e.g., "newest", "cheapest", etc.; may be null).
     * @param onSuccess  callback accepting the list of {@link AdData}.
     * @param onError    callback accepting an error message.
     */
    public void searchAds(String keyword, Long categoryId, Long cityId, Double minPrice, Double maxPrice,
                          String condition, String sortBy, Consumer<List<AdData>> onSuccess,
                          Consumer<String> onError) {
        StringBuilder path = new StringBuilder("/ads");
        appendQuery(path, "keyword", keyword);
        appendQuery(path, "categoryId", categoryId);
        appendQuery(path, "cityId", cityId);
        appendQuery(path, "minPrice", minPrice);
        appendQuery(path, "maxPrice", maxPrice);
        appendQuery(path, "condition", condition);
        appendQuery(path, "sortBy", sortBy);
        ApiClient.send("GET", path.toString(), null,
                response -> handleList(response, onSuccess, onError), onError);
    }

    /**
     * Fetches all pending advertisements (admin only).
     *
     * @param onSuccess callback accepting the list of {@link AdData}.
     * @param onError   callback accepting an error message.
     */
    public void fetchPendingAds(Consumer<List<AdData>> onSuccess, Consumer<String> onError) {
        ApiClient.send("GET", "/ads/pending", null,
                response -> handleList(response, onSuccess, onError), onError);
    }

    /**
     * Fetches advertisements belonging to the currently authenticated user.
     *
     * @param onSuccess callback accepting the list of {@link AdData}.
     * @param onError   callback accepting an error message.
     */
    public void fetchMyAds(Consumer<List<AdData>> onSuccess, Consumer<String> onError) {
        ApiClient.send("GET", "/ads/my-ads", null,
                response -> handleList(response, onSuccess, onError), onError);
    }

    /**
     * Fetches detailed information for a specific advertisement.
     *
     * @param adId      the ID of the ad.
     * @param onSuccess callback accepting the full {@link AdDetailData}.
     * @param onError   callback accepting an error message.
     */
    public void fetchAdDetails(Long adId, Consumer<AdDetailData> onSuccess, Consumer<String> onError) {
        ApiClient.send("GET", "/ads/" + adId, null,
                response -> {
                    if (response.statusCode() == 200) {
                        AdDetailData ad = GSON.fromJson(response.body(), AdDetailData.class);
                        onSuccess.accept(ad);
                    } else {
                        onError.accept(ApiClient.extractErrorMessage(response, "Error fetching ad details: "));
                    }
                }, onError);
    }

    /**
     * Creates a new advertisement.
     *
     * @param newAd     the request data for the new ad.
     * @param onSuccess callback accepting the created {@link AdDetailData}.
     * @param onError   callback accepting an error message.
     */
    public void createAd(AdRequestData newAd, Consumer<AdDetailData> onSuccess, Consumer<String> onError) {
        String jsonBody = GSON.toJson(newAd);
        ApiClient.send("POST", "/ads", jsonBody,
                response -> {
                    if (response.statusCode() == 200 || response.statusCode() == 201) {
                        onSuccess.accept(GSON.fromJson(response.body(), AdDetailData.class));
                    } else {
                        onError.accept(ApiClient.extractErrorMessage(response, "Error creating ad: "));
                    }
                }, onError);
    }

    /**
     * Updates an existing advertisement.
     *
     * @param adId      the ID of the ad to update.
     * @param updatedAd the updated request data.
     * @param onSuccess callback accepting the updated {@link AdDetailData}.
     * @param onError   callback accepting an error message.
     */
    public void updateAd(Long adId, AdRequestData updatedAd, Consumer<AdDetailData> onSuccess, Consumer<String> onError) {
        String jsonBody = GSON.toJson(updatedAd);
        ApiClient.send("PUT", "/ads/" + adId, jsonBody,
                response -> {
                    if (response.statusCode() == 200) {
                        onSuccess.accept(GSON.fromJson(response.body(), AdDetailData.class));
                    } else {
                        onError.accept(ApiClient.extractErrorMessage(response, "Error updating ad: "));
                    }
                }, onError);
    }

    /**
     * Soft‑deletes an advertisement (changes status to DELETED).
     *
     * @param adId      the ID of the ad to delete.
     * @param onSuccess callback accepting a success message.
     * @param onError   callback accepting an error message.
     */
    public void deleteAd(Long adId, Consumer<String> onSuccess, Consumer<String> onError) {
        ApiClient.send("DELETE", "/ads/" + adId, null,
                response -> {
                    if (response.statusCode() == 200 || response.statusCode() == 204) {
                        onSuccess.accept("آگهی با موفقیت حذف شد.");
                    } else {
                        onError.accept(ApiClient.extractErrorMessage(response, "Error deleting ad: "));
                    }
                }, onError);
    }

    /**
     * Marks an ad as sold (status changes to SOLD).
     *
     * @param adId      the ID of the ad.
     * @param onSuccess callback accepting a success message.
     * @param onError   callback accepting an error message.
     */
    public void markAsSold(Long adId, Consumer<String> onSuccess, Consumer<String> onError) {
        ApiClient.send("PUT", "/ads/" + adId + "/mark-as-sold", null,
                response -> {
                    if (response.statusCode() == 200) {
                        onSuccess.accept("آگهی با موفقیت به فروخته شده علامت‌گذاری شد.");
                    } else {
                        onError.accept(ApiClient.extractErrorMessage(response, "Error marking ad as sold: "));
                    }
                }, onError);
    }

    /**
     * Uploads a single image for the specified ad.
     *
     * @param adId      the ad ID.
     * @param file      the image file.
     * @param onSuccess callback accepting a success message.
     * @param onError   callback accepting an error message.
     */
    public void uploadImage(Long adId, File file, Consumer<String> onSuccess, Consumer<String> onError) {
        uploadImages(adId, List.of(file), onSuccess, onError);
    }

    /**
     * Uploads multiple images for the specified ad.
     *
     * @param adId      the ad ID.
     * @param files     the list of image files.
     * @param onSuccess callback accepting a success message.
     * @param onError   callback accepting an error message.
     */
    public void uploadImages(Long adId, List<File> files, Consumer<String> onSuccess, Consumer<String> onError) {
        ApiClient.uploadFiles("/ads/" + adId + "/images", "files", files,
                response -> {
                    if (response.statusCode() == 200 || response.statusCode() == 201) {
                        onSuccess.accept("عکس‌ها با موفقیت آپلود شدند");
                    } else {
                        onError.accept(ApiClient.extractErrorMessage(response, "خطای سرور: "));
                    }
                }, onError);
    }

    /**
     * Updates the status of an ad (admin action). Currently supports "ACTIVE" (approve).
     * This method is deprecated for rejection; use {@link #rejectAd(Long, String, Consumer, Consumer)} instead.
     *
     * @param adId      the ad ID.
     * @param status    the new status (only "ACTIVE" is fully supported).
     * @param onSuccess callback accepting a success message.
     * @param onError   callback accepting an error message.
     */
    public void updateAdStatus(Long adId, String status, Consumer<String> onSuccess, Consumer<String> onError) {
        String path;
        if ("ACTIVE".equals(status)) {
            path = "/ads/" + adId + "/approve";
        } else if ("REJECTED".equals(status)) {
            path = "/ads/" + adId + "/reject?reason=";
        } else {
            onError.accept("Unsupported status change: " + status);
            return;
        }

        ApiClient.send("PUT", path, null,
                response -> {
                    if (response.statusCode() == 200) {
                        onSuccess.accept("وضعیت آگهی با موفقیت به " + status + " تغییر کرد.");
                    } else {
                        onError.accept(ApiClient.extractErrorMessage(response, "خطا در تغییر وضعیت: "));
                    }
                }, onError);
    }

    /**
     * Appends a query parameter to the URL builder if the value is not null/empty.
     *
     * @param path the StringBuilder containing the URL.
     * @param key  the parameter key.
     * @param value the parameter value (may be null).
     */
    private void appendQuery(StringBuilder path, String key, Object value) {
        if (value == null || (value instanceof String text && text.isBlank())) {
            return;
        }
        path.append(path.indexOf("?") >= 0 ? "&" : "?");
        path.append(key).append("=")
                .append(URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8));
    }

    /**
     * Handles the HTTP response for list endpoints, deserializing into a list of {@link AdData}.
     *
     * @param response  the HTTP response.
     * @param onSuccess callback accepting the list.
     * @param onError   callback accepting an error message.
     */
    private void handleList(HttpResponse<String> response, Consumer<List<AdData>> onSuccess, Consumer<String> onError) {
        if (response.statusCode() == 200) {
            Type listType = new TypeToken<List<AdData>>() {
            }.getType();
            List<AdData> ads = GSON.fromJson(response.body(), listType);
            onSuccess.accept(ads);
        } else {
            onError.accept(ApiClient.extractErrorMessage(response, "Error fetching ads: "));
        }
    }

    /**
     * Rejects an ad with a given reason.
     *
     * @param adId      the ID of the ad to reject.
     * @param reason    the rejection reason.
     * @param onSuccess callback accepting a success message.
     * @param onError   callback accepting an error message.
     */
    public void rejectAd(Long adId, String reason, Consumer<String> onSuccess, Consumer<String> onError) {
        String jsonBody = GSON.toJson(java.util.Map.of("reason", reason));

        ApiClient.send("PUT", "/ads/" + adId + "/reject", jsonBody,
                response -> {
                    if (response.statusCode() == 200 || response.statusCode() == 201) {
                        onSuccess.accept("آگهی با موفقیت رد شد.");
                    } else {
                        onError.accept(ApiClient.extractErrorMessage(response, "خطا در رد آگهی: "));
                    }
                }, onError);
    }
}