package divar.aut.frontend;

import divar.aut.frontend.ui.AdData;
import javafx.application.Platform;
import com.google.gson.Gson; // فرض بر این است که Gson دارید
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;
import java.lang.reflect.Type;
public class AdService {
    private static final String BASE_URL = "http://localhost:8080/ads";
    private final HttpClient httpClient;
    private final Gson gson;
    private final String token;
    public AdService(String token) {
        this.token = token;
        httpClient = HttpClient.newHttpClient();
        gson = new Gson();
    }
    public void fetchAds(int page,Consumer<List<AdData>> onSuccess, Consumer<String> onError)
    {
        String urlWithPage = BASE_URL + "?page=" + page;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if(response.statusCode() == 200)
                    {
                        Type listType = new TypeToken<List<AdData>>(){}.getType();
                        List<AdData> ads = gson.fromJson(response.body(), listType);
                        Platform.runLater(() -> onSuccess.accept(ads));
                    }
                    else
                    {
                        Platform.runLater(() -> onError.accept("Error fetching ads: " + response.statusCode()));
                    }
                }).exceptionally(ex -> {
                    Platform.runLater(() -> onError.accept(ex.getMessage()));
                    return null;
                });
    }
    public void createAd(AdData newAd, Consumer<AdData> onSuccess, Consumer<String> onError)
    {
        String jsonBody = gson.toJson(newAd);
        System.out.println("JSON Payload: " + jsonBody);
        System.out.println("Sending Token: " + this.token);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if(response.statusCode() == 200)
                    {
                        AdData createdAd = gson.fromJson(response.body(), AdData.class);
                        Platform.runLater(() -> onSuccess.accept(createdAd));
                    }
                    else
                    {
                        Platform.runLater(() -> onError.accept("Error creating ad: " + response.statusCode()));
                    }
                });
    }
    public void uploadImage(Long adId, File file, Consumer<String> onSuccess, Consumer<String> onError) {
        try {
            String boundary = "---" + System.currentTimeMillis() + "---";
            byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());

            String part1 = "--" + boundary + "\r\n" +
                    "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n" +
                    "Content-Type: application/octet-stream\r\n\r\n";
            String part2 = "\r\n--" + boundary + "--\r\n";

            byte[] body = (part1).getBytes();
            byte[] finalBody = new byte[body.length + fileBytes.length + part2.getBytes().length];
            System.arraycopy(body, 0, finalBody, 0, body.length);
            System.arraycopy(fileBytes, 0, finalBody, body.length, fileBytes.length);
            System.arraycopy(part2.getBytes(), 0, finalBody, body.length + fileBytes.length, part2.getBytes().length);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/ads/" + adId + "/image"))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(finalBody))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            Platform.runLater(() -> onSuccess.accept("عکس با موفقیت آپلود شد"));
                        } else {
                            Platform.runLater(() -> onError.accept("خطای سرور: " + response.statusCode()));
                        }
                    }).exceptionally(ex -> {
                        Platform.runLater(() -> onError.accept(ex.getMessage()));
                        return null;
                    });

        } catch (IOException e) {
            onError.accept("خطا در خواندن فایل: " + e.getMessage());
        }
    }
}
