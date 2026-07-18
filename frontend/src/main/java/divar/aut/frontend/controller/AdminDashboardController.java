package divar.aut.frontend.controller;

import divar.aut.frontend.model.*;
import divar.aut.frontend.net.*;
import divar.aut.frontend.ui.ViewManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AdminDashboardController {
    @FXML private FlowPane statsPane;
    @FXML private FlowPane adminAdGrid;
    @FXML private ListView<UserData> usersList;
    @FXML private ListView<CategoryData> categoriesList;
    @FXML private ListView<CityData> citiesList;
    @FXML private TextField categoryNameField;
    @FXML private TextField cityNameField;
    @FXML private Label statusLabel;

    private final AdminService adminService = new AdminService();
    private final CategoryService categoryService = new CategoryService();
    private final CityService cityService = new CityService();
    private AdService adService;
    private ViewManager viewManager;

    public void setDependencies(AdService adService, ViewManager viewManager) {
        this.adService = adService;
        this.viewManager = viewManager;
        configureLists();
        refreshAll();
    }

    private void configureLists() {
        usersList.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(UserData user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) { setGraphic(null); return; }
                Label label = new Label(user.fullname() + " (@" + user.username() + ") - " + user.role());
                label.getStyleClass().add("admin-list-label");
                Button action = new Button(user.blocked() ? "رفع مسدودی" : "مسدود کردن");
                action.getStyleClass().add(user.blocked() ? "admin-action-button" : "admin-danger-button");
                action.setDisable("ADMIN".equals(user.role()));
                action.setOnAction(e -> adminService.setUserBlocked(user.id(), !user.blocked(),
                        ok -> { showSuccess(ok); loadUsers(); }, AdminDashboardController.this::showError));
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                HBox row = new HBox(12, label, spacer, action);
                row.setAlignment(Pos.CENTER_RIGHT);
                row.getStyleClass().add("admin-list-row");
                setGraphic(row);
            }
        });
        categoriesList.setCellFactory(list -> metadataCell(true));
        citiesList.setCellFactory(list -> metadataCell(false));
    }

    @SuppressWarnings("unchecked")
    private <T> ListCell<T> metadataCell(boolean category) {
        return new ListCell<>() {
            @Override protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Long id = category ? ((CategoryData) item).id() : ((CityData) item).id();
                String name = category ? ((CategoryData) item).name() : ((CityData) item).name();
                Label label = new Label(name);
                label.getStyleClass().add("admin-list-label");
                Button edit = new Button("ویرایش");
                Button delete = new Button("حذف");
                edit.getStyleClass().add("admin-action-button");
                delete.getStyleClass().add("admin-danger-button");
                edit.setOnAction(e -> editMetadata(category, id, name));
                delete.setOnAction(e -> deleteMetadata(category, id));
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                HBox row = new HBox(10, label, spacer, edit, delete);
                row.setAlignment(Pos.CENTER_RIGHT);
                row.getStyleClass().add("admin-list-row");
                setGraphic(row);
            }
        };
    }

    private void refreshAll() { loadStats(); loadAds(); loadUsers(); loadCategories(); loadCities(); }

    private void loadStats() {
        adminService.getStats(stats -> {
            statsPane.getChildren().setAll(
                    stat("کاربران", stats.totalUsers()), stat("مسدود", stats.blockedUsers()),
                    stat("آگهی‌ها", stats.totalAds()), stat("در انتظار", stats.pendingAds()),
                    stat("فعال", stats.activeAds()), stat("فروخته", stats.soldAds()),
                    stat("دسته‌بندی", stats.categories()), stat("شهر", stats.cities()));
        }, this::showError);
    }

    private VBox stat(String title, long value) {
        Label number = new Label(String.valueOf(value));
        number.getStyleClass().add("admin-stat-number");
        Label name = new Label(title);
        name.getStyleClass().add("admin-stat-title");
        VBox card = new VBox(5, number, name);
        card.setPrefWidth(160);
        card.getStyleClass().add("admin-stat-card");
        return card;
    }

    private void loadAds() { adminService.listAds(this::renderAds, this::showError); }
    private void loadUsers() { adminService.listUsers(usersList.getItems()::setAll, this::showError); }
    private void loadCategories() { categoryService.listAll(categoriesList.getItems()::setAll, this::showError); }
    private void loadCities() { cityService.listAll(citiesList.getItems()::setAll, this::showError); }

    private void renderAds(List<AdData> ads) {
        adminAdGrid.getChildren().clear();
        java.util.List<String> order = java.util.List.of("PENDING_REVIEW", "ACTIVE", "REJECTED", "DELETED", "SOLD");
        java.util.Map<String, String> titles = java.util.Map.of(
                "PENDING_REVIEW", "در انتظار بررسی",
                "ACTIVE", "فعال",
                "REJECTED", "رد شده",
                "DELETED", "حذف شده",
                "SOLD", "فروخته شده");
        for (String status : order) {
            List<AdData> group = ads.stream().filter(ad -> status.equals(ad.status())).toList();
            if (group.isEmpty()) continue;
            Label header = new Label(titles.get(status) + " (" + group.size() + ")");
            header.setStyle("-fx-text-fill: #f0f0f0; -fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 8 0 2 0;");
            HBox headerBox = new HBox(header);
            headerBox.setPrefWidth(Double.MAX_VALUE);
            headerBox.setMaxWidth(Double.MAX_VALUE);
            adminAdGrid.getChildren().add(headerBox);
            for (AdData ad : group) {
                Node card = buildCard(ad);
                if (card != null) adminAdGrid.getChildren().add(card);
            }
        }
        if (adminAdGrid.getChildren().isEmpty()) {
            Label empty = new Label("آگهی‌ای وجود ندارد");
            empty.setStyle("-fx-text-fill: #777; -fx-font-size: 14px;");
            adminAdGrid.getChildren().add(empty);
        }
    }

    private Node buildCard(AdData data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdCard.fxml"));
            Parent card = loader.load();
            loader.<AdCardController>getController().setData(data);
            ContextMenu menu = new ContextMenu();
            MenuItem delete = new MenuItem("حذف توسط مدیر");
            delete.setOnAction(e -> requestAdminDelete(data.id()));
            menu.getItems().add(delete);
            card.setOnContextMenuRequested(e -> menu.show(card, e.getScreenX(), e.getScreenY()));
            card.setOnMouseClicked(e -> openAdDetails(data));
            return card;
        } catch (IOException e) { showError("خطا در نمایش آگهی"); return null; }
    }

    private void requestAdminDelete(Long adId) {
        adminService.deleteAd(adId,
                ok -> { showSuccess(ok); refreshAll(); },
                error -> Platform.runLater(() -> showError(error)));
    }

    private void openAdDetails(AdData data) {
        adService.fetchAdDetails(data.id(), detail -> Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdDetails.fxml"));
                Parent root = loader.load();
                loader.<AdDetailsController>getController().setData(detail, adService, "ADMIN", false,
                        this::refreshAll, viewManager, this::requestAdminDelete);
                Stage stage = new Stage(); stage.setTitle("مدیریت آگهی: " + data.title()); stage.setScene(new Scene(root)); stage.initModality(Modality.APPLICATION_MODAL); stage.show();
            } catch (IOException e) { showError("خطا در باز کردن آگهی"); }
        }), this::showError);
    }

    @FXML private void addCategory() { String name = categoryNameField.getText().trim(); if (!name.isEmpty()) adminService.createCategory(name, c -> { categoryNameField.clear(); loadCategories(); loadStats(); }, this::showError); }
    @FXML private void addCity() { String name = cityNameField.getText().trim(); if (!name.isEmpty()) adminService.createCity(name, c -> { cityNameField.clear(); loadCities(); loadStats(); }, this::showError); }

    private void editMetadata(boolean category, Long id, String currentName) {
        TextInputDialog dialog = new TextInputDialog(currentName);
        dialog.setTitle(category ? "ویرایش دسته‌بندی" : "ویرایش شهر");
        dialog.setHeaderText(category ? "ویرایش نام دسته‌بندی" : "ویرایش نام شهر");
        dialog.setContentText("نام جدید:");
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.OK)).setText("ذخیره");
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("انصراف");
        styleDialog(dialog);
        Optional<String> result = dialog.showAndWait();
        result.map(String::trim).filter(name -> !name.isEmpty()).ifPresent(name -> {
            if (category) adminService.updateCategory(id, name, c -> {
                loadCategories();
                loadAds();
                showSuccess("دسته‌بندی با موفقیت ویرایش شد");
            }, this::showError);
            else adminService.updateCity(id, name, c -> {
                loadCities();
                loadAds();
                showSuccess("شهر با موفقیت ویرایش شد");
            }, this::showError);
        });
    }

    private void styleDialog(Dialog<?> dialog) {
        dialog.initOwner(statusLabel.getScene().getWindow());
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/Dialog.css").toExternalForm());
        dialog.getDialogPane().setNodeOrientation(javafx.geometry.NodeOrientation.RIGHT_TO_LEFT);
    }

    private void deleteMetadata(boolean category, Long id) {
        if (category) {
            adminService.getCategoryUsage(id,
                    usage -> showMetadataDeleteDialog(true, id, usage.affectedAds()), this::showError);
        } else {
            adminService.getCityUsage(id,
                    usage -> showMetadataDeleteDialog(false, id, usage.affectedAds()), this::showError);
        }
    }

    private void showMetadataDeleteDialog(boolean category, Long id, long affectedAds) {
        String typeName = category ? "دسته‌بندی" : "شهر";
        if (affectedAds == 0) {
            Dialog<ButtonType> confirmation = new Dialog<>();
            confirmation.setTitle("حذف " + typeName);
            confirmation.setHeaderText("این " + typeName + " در هیچ آگهی‌ای استفاده نشده است.");
            Label message = new Label("آیا از حذف آن مطمئن هستید؟");
            message.getStyleClass().add("dialog-label");
            VBox content = new VBox(message);
            content.getStyleClass().add("dialog-content");
            confirmation.getDialogPane().setContent(content);
            ButtonType delete = new ButtonType("حذف", ButtonBar.ButtonData.OK_DONE);
            confirmation.getDialogPane().getButtonTypes().addAll(delete,
                    new ButtonType("انصراف", ButtonBar.ButtonData.CANCEL_CLOSE));
            styleDialog(confirmation);
            confirmation.showAndWait().filter(delete::equals).ifPresent(button ->
                    executeMetadataDeletion(category, id, "DELETE_ADS", null));
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("حذف " + typeName);
        dialog.setHeaderText(affectedAds + " آگهی از این " + typeName + " استفاده می‌کنند");

        RadioButton reassignOption = new RadioButton("انتقال آگهی‌ها به " + typeName + " دیگر");
        RadioButton deleteAdsOption = new RadioButton("حذف دائمی آگهی‌ها همراه با " + typeName);
        reassignOption.getStyleClass().add("dialog-label");
        deleteAdsOption.getStyleClass().add("danger-option");
        ToggleGroup choices = new ToggleGroup();
        reassignOption.setToggleGroup(choices);
        deleteAdsOption.setToggleGroup(choices);
        reassignOption.setSelected(true);

        ComboBox<MetadataOption> replacementCombo = new ComboBox<>();
        replacementCombo.setMaxWidth(Double.MAX_VALUE);
        replacementCombo.setPromptText(typeName + " جایگزین را انتخاب کنید");
        if (category) {
            categoriesList.getItems().stream()
                    .filter(item -> !item.id().equals(id))
                    .map(item -> new MetadataOption(item.id(), item.name()))
                    .forEach(replacementCombo.getItems()::add);
        } else {
            citiesList.getItems().stream()
                    .filter(item -> !item.id().equals(id))
                    .map(item -> new MetadataOption(item.id(), item.name()))
                    .forEach(replacementCombo.getItems()::add);
        }
        replacementCombo.setCellFactory(list -> metadataOptionCell());
        replacementCombo.setButtonCell(metadataOptionCell());
        replacementCombo.disableProperty().bind(deleteAdsOption.selectedProperty());

        Label warning = new Label("حذف آگهی‌ها دائمی است و پیام‌ها، علاقه‌مندی‌ها، امتیازها و تصاویر مرتبط را نیز حذف می‌کند.");
        warning.setWrapText(true);
        warning.getStyleClass().add("danger-message");
        warning.visibleProperty().bind(deleteAdsOption.selectedProperty());
        warning.managedProperty().bind(deleteAdsOption.selectedProperty());

        Label validationMessage = new Label();
        validationMessage.setWrapText(true);
        validationMessage.getStyleClass().add("danger-message");
        validationMessage.setVisible(false);
        validationMessage.setManaged(false);

        VBox content = new VBox(12, reassignOption, replacementCombo, deleteAdsOption, warning, validationMessage);
        content.getStyleClass().add("dialog-content");
        content.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        dialog.getDialogPane().setContent(content);
        ButtonType confirm = new ButtonType("ادامه", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirm,
                new ButtonType("انصراف", ButtonBar.ButtonData.CANCEL_CLOSE));
        styleDialog(dialog);

        Button confirmButton = (Button) dialog.getDialogPane().lookupButton(confirm);
        confirmButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (reassignOption.isSelected() && replacementCombo.getValue() == null) {
                validationMessage.setText("لطفاً " + typeName + " جایگزین را انتخاب کنید.");
                validationMessage.setVisible(true);
                validationMessage.setManaged(true);
                event.consume();
            }
        });

        dialog.showAndWait().filter(confirm::equals).ifPresent(button -> {
            if (deleteAdsOption.isSelected()) {
                executeMetadataDeletion(category, id, "DELETE_ADS", null);
            } else {
                executeMetadataDeletion(category, id, "REASSIGN", replacementCombo.getValue().id());
            }
        });
    }

    private ListCell<MetadataOption> metadataOptionCell() {
        return new ListCell<>() {
            @Override protected void updateItem(MetadataOption item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.name());
            }
        };
    }

    private void executeMetadataDeletion(boolean category, Long id, String strategy, Long replacementId) {
        java.util.function.Consumer<String> success = message -> {
            refreshAll();
            showSuccess(category ? "دسته‌بندی با موفقیت حذف شد" : "شهر با موفقیت حذف شد");
        };
        if (category) {
            adminService.deleteCategory(id, strategy, replacementId, success, this::showError);
        } else {
            adminService.deleteCity(id, strategy, replacementId, success, this::showError);
        }
    }

    private record MetadataOption(Long id, String name) {
        @Override public String toString() { return name; }
    }

    private void showSuccess(String message) { statusLabel.setText(message); statusLabel.setStyle("-fx-text-fill: #4ade80;"); }
    private void showError(String message) { statusLabel.setText("خطا: " + message); statusLabel.setStyle("-fx-text-fill: #ff5a5a;"); }
    @FXML private void goBack() { viewManager.toMain(); }
}
