package divar.aut.frontend.controller;

import divar.aut.frontend.model.AdDetailData;
import divar.aut.frontend.model.AdRequestData;
import divar.aut.frontend.model.CategoryData;
import divar.aut.frontend.model.CityData;
import divar.aut.frontend.net.AdService;
import divar.aut.frontend.net.CategoryService;
import divar.aut.frontend.net.CityService;
import divar.aut.frontend.ui.ViewManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    private final List<CategoryData> categoryList = new ArrayList<>();
    private final List<CityData> cityList = new ArrayList<>();
    private File selectedImageFile = null;
    private ViewManager viewManager;
    private AdDetailData editingAd;
    private String pendingCategoryName;
    private String pendingCityName;

    public void setDependencies(AdService adService, ViewManager viewManager) {
        this.adService = adService;
        this.viewManager = viewManager;
    }

    public void setData(AdDetailData adDetail) {
        this.editingAd = adDetail;
        this.pendingCategoryName = adDetail.categoryName();
        this.pendingCityName = adDetail.cityName();

        titleField.setText(adDetail.title());
        descriptionArea.setText(adDetail.description());
        priceField.setText(String.valueOf(adDetail.price()));
        conditionCombo.getSelectionModel().select(mapConditionToLabel(adDetail.itemCondition()));

        if (!categoryList.isEmpty()) {
            categoryCombo.getSelectionModel().select(pendingCategoryName);
        }
        if (!cityList.isEmpty()) {
            locationCombo.getSelectionModel().select(pendingCityName);
        }
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
                    categoryList.clear();
                    categoryList.addAll(categories);
                    categoryCombo.getItems().setAll(categories.stream().map(CategoryData::name).toList());
                    if (pendingCategoryName != null) {
                        categoryCombo.getSelectionModel().select(pendingCategoryName);
                    }
                    if (categoryCombo.getSelectionModel().isEmpty() && !categoryCombo.getItems().isEmpty()) {
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
                    cityList.clear();
                    cityList.addAll(cities);
                    locationCombo.getItems().setAll(cities.stream().map(CityData::name).toList());
                    if (pendingCityName != null) {
                        locationCombo.getSelectionModel().select(pendingCityName);
                    }
                    if (locationCombo.getSelectionModel().isEmpty() && !locationCombo.getItems().isEmpty()) {
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

        long selectedCategoryId = findSelectedCategoryId();
        long selectedCityId = findSelectedCityId();
        if (selectedCategoryId <= 0 || selectedCityId <= 0) {
            statusLabel.setText("لطفا دسته‌بندی و شهر معتبر انتخاب کنید.");
            statusLabel.setStyle("-fx-text-fill: #ff5a5a;");
            return;
        }

        AdRequestData requestData = new AdRequestData(
                selectedCategoryId,
                selectedCityId,
                titleField.getText(),
                descriptionArea.getText(),
                priceValue,
                mapLabelToCondition(conditionCombo.getValue())
        );

        if (editingAd != null) {
            adService.updateAd(editingAd.id(), requestData,
                    updatedAd -> handlePostSuccess(updatedAd),
                    error -> Platform.runLater(() -> handleError("خطا در ویرایش: " + error)));
        } else {
            adService.createAd(requestData,
                    createdAd -> handlePostSuccess(createdAd),
                    error -> Platform.runLater(() -> handleError("خطا در ثبت: " + error)));
        }
    }

    private void handlePostSuccess(AdDetailData adDetail) {
        if (selectedImageFile != null) {
            adService.uploadImage(adDetail.id(), selectedImageFile,
                    msg -> Platform.runLater(() -> viewManager.toMain()),
                    err -> Platform.runLater(() -> handleError("آگهی ثبت شد اما عکس خطا خورد: " + err))
            );
        } else {
            Platform.runLater(() -> viewManager.toMain());
        }
    }

    private void handleError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #ff5a5a;");
    }

    private long findSelectedCategoryId() {
        String selectedName = categoryCombo.getValue();
        return categoryList.stream()
                .filter(c -> c.name().equals(selectedName))
                .map(CategoryData::id)
                .findFirst()
                .orElse(-1L);
    }

    private long findSelectedCityId() {
        String selectedName = locationCombo.getValue();
        return cityList.stream()
                .filter(c -> c.name().equals(selectedName))
                .map(CityData::id)
                .findFirst()
                .orElse(-1L);
    }

    private String mapLabelToCondition(String label) {
        if (label == null) return "USED";
        return switch (label) {
            case "نو" -> "NEW";
            case "در حد نو", "کارکرده" -> "USED";
            default -> label.toUpperCase();
        };
    }

    private String mapConditionToLabel(String condition) {
        if (condition == null) return "در حد نو";
        return switch (condition) {
            case "NEW" -> "نو";
            case "USED" -> "در حد نو";
            default -> condition;
        };
    }
}
