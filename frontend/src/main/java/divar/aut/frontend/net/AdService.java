package divar.aut.frontend.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import divar.aut.frontend.model.AdData;

import java.io.File;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;

/** Talks to /ads/**. */
public class AdService {

    private static final Gson GSON = new Gson();

    public void fetchAds(int page, Consumer<List<AdData>> onSuccess, Consumer<String> onError) {
        ApiClient.send("GET", "/ads?page=" + page, null,
                response -> handleList(response, onSuccess, onError), onError);
    }

    public void createAd(AdData newAd, Consumer<AdData> onSuccess, Consumer<String> onError) {
        String jsonBody = GSON.toJson(newAd);
        ApiClient.send("POST", "/ads", jsonBody,
                response -> {
                    if (response.statusCode() == 200) {
                        onSuccess.accept(GSON.fromJson(response.body(), AdData.class));
                    } else {
                        onError.accept(ApiClient.extractErrorMessage(response, "Error creating ad: "));
                    }
                }, onError);
    }

    public void uploadImage(Long adId, File file, Consumer<String> onSuccess, Consumer<String> onError) {
        ApiClient.uploadFile("/ads/" + adId + "/image", "file", file,
                response -> {
                    if (response.statusCode() == 200) {
                        onSuccess.accept("عکس با موفقیت آپلود شد");
                    } else {
                        onError.accept(ApiClient.extractErrorMessage(response, "خطای سرور: "));
                    }
                }, onError);
    }

    public void fetchPendingAds(Consumer<List<AdData>> onSuccess, Consumer<String> onError) {
        ApiClient.send("GET", "/ads/pending", null,
                response -> handleList(response, onSuccess, onError), onError);
    }

    public void updateAdStatus(Long adId, String status, Consumer<String> onSuccess, Consumer<String> onError) {
        ApiClient.send("PUT", "/ads/" + adId + "/status?status=" + status, null,
                response -> {
                    if (response.statusCode() == 200) {
                        onSuccess.accept("وضعیت آگهی با موفقیت به " + status + " تغییر کرد.");
                    } else {
                        onError.accept(ApiClient.extractErrorMessage(response, "خطا در تغییر وضعیت: "));
                    }
                }, onError);
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
}
