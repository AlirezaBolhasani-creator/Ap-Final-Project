package divar.aut.frontend.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import divar.aut.frontend.model.AdData;
import divar.aut.frontend.model.AdDetailData;
import divar.aut.frontend.model.AdRequestData;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

/** Talks to /ads/**. */
public class AdService {

    private static final Gson GSON = new Gson();

    public void fetchAds(int page, Consumer<List<AdData>> onSuccess, Consumer<String> onError) {
        ApiClient.send("GET", "/ads?page=" + page, null,
                response -> handleList(response, onSuccess, onError), onError);
    }


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

    public void fetchPendingAds(Consumer<List<AdData>> onSuccess, Consumer<String> onError) {
        ApiClient.send("GET", "/ads/pending", null,
                response -> handleList(response, onSuccess, onError), onError);
    }

    public void fetchMyAds(Consumer<List<AdData>> onSuccess, Consumer<String> onError) {
        ApiClient.send("GET", "/ads/my-ads", null,
                response -> handleList(response, onSuccess, onError), onError);
    }

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

    public void uploadImage(Long adId, File file, Consumer<String> onSuccess, Consumer<String> onError) {
        uploadImages(adId, List.of(file), onSuccess, onError);
    }

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

    private void appendQuery(StringBuilder path, String key, Object value) {
        if (value == null || (value instanceof String text && text.isBlank())) {
            return;
        }
        path.append(path.indexOf("?") >= 0 ? "&" : "?");
        path.append(key).append("=")
                .append(URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8));
    }

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
    public void rejectAd(Long adId, String reason, Consumer<String> onSuccess, Consumer<String> onError) {
        //just fix at frontend for now after that we will fix this at backend.
        String requestBody = String.format("{\"rejectionReason\": \"%s\"}", reason);


    }
}
