package divar.aut.frontend.controller;

import divar.aut.frontend.net.AdService;
import divar.aut.frontend.net.CategoryService;
import divar.aut.frontend.net.CityService;
import divar.aut.frontend.model.AdData;
import divar.aut.frontend.ui.ViewManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class PostAdController implements Initializable {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> locationCombo;
    @FXML private ComboBox<String> conditionCombo;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private StackPane imageUploadBox;
    @FXML private Label statusLabel;

    private AdService adService;
    private final CategoryService categoryService = new CategoryService();
    private final CityService cityService = new CityService();
    private File selectedImageFile = null;
    private ViewManager viewManager;

    public void setDependencies(AdService adService, ViewManager viewManager) {
        this.adService = adService;
        this.viewManager = viewManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        conditionCombo.getItems().addAll("نو", "در حد نو", "کارکرده");
        conditionCombo.getSelectionModel().select(0);
        loadCategories();
        loadCities();
    }

    private void loadCategories() {
        categoryService.listAll(
                categories -> {
                    categoryCombo.getItems().setAll(categories.stream().map(c -> c.name()).toList());
                    if (!categoryCombo.getItems().isEmpty()) {
                        categoryCombo.getSelectionModel().selectFirst();
                    }
                },
                error -> {
                    statusLabel.setText("خطا در دریافت دسته‌بندی‌ها: " + error);
                    statusLabel.setStyle("-fx-text-fill: #ff5a5a;");
                }
        );
    }

    private void loadCities() {
        cityService.listAll(
                cities -> {
                    locationCombo.getItems().setAll(cities.stream().map(c -> c.name()).toList());
                    if (!locationCombo.getItems().isEmpty()) {
                        locationCombo.getSelectionModel().selectFirst();
                    }
                },
                error -> {
                    statusLabel.setText("خطا در دریافت شهرها: " + error);
                    statusLabel.setStyle("-fx-text-fill: #ff5a5a;");
                }
        );
    }

    @FXML
    public void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("انتخاب عکس");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(imageUploadBox.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            statusLabel.setText("عکس انتخاب شد: " + file.getName());
            statusLabel.setStyle("-fx-text-fill: #4CAF50;");
        }
    }

    @FXML
    public void handleClear() {
        titleField.clear();
        descriptionArea.clear();
        priceField.clear();
        if (locationCombo != null) locationCombo.getSelectionModel().clearSelection();
        if (conditionCombo != null) conditionCombo.getSelectionModel().clearSelection();
        if (categoryCombo != null) categoryCombo.getSelectionModel().clearSelection();
        selectedImageFile = null;
        statusLabel.setText("");
        statusLabel.setStyle("-fx-text-fill: #ff5a5a;");
    }

    @FXML
    public void handleSubmit() {
        // 1. Validate required fields
        if (titleField.getText().isBlank() || priceField.getText().isBlank() ||
                locationCombo.getValue() == null || categoryCombo.getValue() == null) {
            statusLabel.setText("لطفا تمام فیلدهای ستاره‌دار را پر کنید!");
            statusLabel.setStyle("-fx-text-fill: #ff5a5a;");
            return;
        }

        double priceValue = 0.0;
        try {
            priceValue = Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            statusLabel.setText("قیمت باید عدد باشد!");
            statusLabel.setStyle("-fx-text-fill: #ff5a5a;");
            return;
        }

        statusLabel.setText("در حال ارسال...");
        statusLabel.setStyle("-fx-text-fill: white;");

        // 2. Build AdData WITH the category included
        AdData newAd = new AdData(
                titleField.getText(),
                priceValue,
                locationCombo.getValue(),
                conditionCombo.getValue(),
                categoryCombo.getValue(),
                null,
                0,
                null
        );

        // 3. Send request and handle navigation
        adService.createAd(newAd,
                createdAd -> {
                    if (selectedImageFile != null) {
                        adService.uploadImage(createdAd.id(), selectedImageFile,
                                msg -> {
                                    Platform.runLater(() -> viewManager.toMain());
                                },
                                err -> {
                                    Platform.runLater(() -> {
                                        statusLabel.setText("آگهی ثبت شد اما عکس خطا خورد: " + err);
                                        statusLabel.setStyle("-fx-text-fill: #ff5a5a;");
                                    });
                                }
                        );
                    } else {
                        // Success without Image -> Go back to Main immediately
                        Platform.runLater(() -> viewManager.toMain());
                    }
                },
                error -> {
                    Platform.runLater(() -> {
                        statusLabel.setText("خطا در ثبت: " + error);
                        statusLabel.setStyle("-fx-text-fill: #ff5a5a;");
                    });
                }
        );
    }
}