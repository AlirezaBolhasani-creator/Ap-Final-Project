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

/**
 * JavaFX controller for the "Post Ad" screen.
 * <p>
 * Allows users to create a new advertisement or edit an existing one.
 * The form includes fields for title, description, price, location,
 * condition, and category. Users can also upload multiple images.
 * On submission, the ad is sent to the backend via {@link AdService}.
 * This controller also supports editing by pre‑populating fields with
 * existing data when {@link #setData(AdDetailData)} is called.
 * </p>
 */
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
    private final List<File> selectedImageFiles = new ArrayList<>();
    private ViewManager viewManager;
    private AdDetailData editingAd;
    private String pendingCategoryName;
    private String pendingCityName;

    /**
     * Injects required dependencies after the FXML is loaded.
     *
     * @param adService   the service for ad operations.
     * @param viewManager the navigation manager.
     */
    public void setDependencies(AdService adService, ViewManager viewManager) {
        this.adService = adService;
        this.viewManager = viewManager;
    }

    /**
     * Pre‑populates the form with existing ad data for editing.
     * Called when the controller is used in edit mode.
     *
     * @param adDetail the existing ad data to edit.
     */
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

    /**
     * Initializes the controller after FXML loading.
     * Sets up condition combo box and loads categories and cities.
     *
     * @param location  the location used to resolve relative paths (unused).
     * @param resources the resources used to localize the root object (unused).
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        conditionCombo.getItems().addAll("نو", "کارکرده");
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
                    setStatusError();
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
                    setStatusError();
                }
        );
    }

    /**
     * Opens a file chooser for selecting image files.
     * Supports .png, .jpg, and .jpeg. Stores the chosen files for upload.
     */
    @FXML
    public void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("انتخاب عکس");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        List<File> files = fileChooser.showOpenMultipleDialog(imageUploadBox.getScene().getWindow());
        if (files != null && !files.isEmpty()) {
            selectedImageFiles.clear();
            selectedImageFiles.addAll(files);
            statusLabel.setText(files.size() + " عکس انتخاب شد");
            setStatusOk();
        }
    }

    /**
     * Navigates back to the main screen without saving.
     */
    @FXML
    private void handleBack() {
        if (viewManager != null) viewManager.toMain();
    }

    /**
     * Clears all input fields, the selected images, and the status message.
     */
    @FXML
    public void handleClear() {
        titleField.clear();
        descriptionArea.clear();
        priceField.clear();
        if (locationCombo != null) locationCombo.getSelectionModel().clearSelection();
        if (conditionCombo != null) conditionCombo.getSelectionModel().clearSelection();
        if (categoryCombo != null) categoryCombo.getSelectionModel().clearSelection();
        selectedImageFiles.clear();
        statusLabel.setText("");
        setStatusError();
    }

    /**
     * Submits the ad data to the backend.
     * Validates required fields, converts inputs, and creates or updates
     * the ad via {@link AdService}. If images are selected, they are uploaded
     * after a successful ad creation/update. On success, navigates back to
     * the main screen.
     */
    @FXML
    public void handleSubmit() {
        // 1. Validate required fields
        if (titleField.getText().isBlank() || priceField.getText().isBlank() ||
                locationCombo.getValue() == null || categoryCombo.getValue() == null) {
            statusLabel.setText("لطفا تمام فیلدهای ستاره‌دار را پر کنید!");
            setStatusError();
            return;
        }

        double priceValue = 0.0;
        try {
            priceValue = Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            statusLabel.setText("قیمت باید عدد باشد!");
            setStatusError();
            return;
        }

        statusLabel.setText("در حال ارسال...");
        setStatusInfo();

        long selectedCategoryId = findSelectedCategoryId();
        long selectedCityId = findSelectedCityId();
        if (selectedCategoryId <= 0 || selectedCityId <= 0) {
            statusLabel.setText("لطفا دسته‌بندی و شهر معتبر انتخاب کنید.");
            setStatusError();
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
        if (!selectedImageFiles.isEmpty()) {
            adService.uploadImages(adDetail.id(), selectedImageFiles,
                    msg -> Platform.runLater(() -> viewManager.toMain()),
                    err -> Platform.runLater(() -> handleError("آگهی ثبت شد اما عکس‌ها خطا خوردند: " + err))
            );
        } else {
            Platform.runLater(() -> viewManager.toMain());
        }
    }

    private void handleError(String message) {
        statusLabel.setText(message);
        setStatusError();
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
            case "کارکرده" -> "USED";
            default -> label.toUpperCase();
        };
    }

    private String mapConditionToLabel(String condition) {
        if (condition == null) return "کارکرده";
        return switch (condition) {
            case "NEW" -> "نو";
            case "USED" -> "کارکرده";
            default -> condition;
        };
    }

    private void setStatusError() {
        statusLabel.getStyleClass().setAll("status-danger");
    }

    private void setStatusOk() {
        statusLabel.getStyleClass().setAll("status-success");
    }

    private void setStatusInfo() {
        statusLabel.getStyleClass().setAll("status-warning");
    }
}