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
        for (AdData ad : ads) {
            Node card = buildCard(ad);
            if (card != null) adminAdGrid.getChildren().add(card);
        }
    }

    private Node buildCard(AdData data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdCard.fxml"));
            Parent card = loader.load();
            loader.<AdCardController>getController().setData(data);
            ContextMenu menu = new ContextMenu();
            MenuItem delete = new MenuItem("حذف توسط مدیر");
            delete.setOnAction(e -> adminService.deleteAd(data.id(), ok -> { showSuccess(ok); refreshAll(); }, this::showError));
            menu.getItems().add(delete);
            card.setOnContextMenuRequested(e -> menu.show(card, e.getScreenX(), e.getScreenY()));
            card.setOnMouseClicked(e -> openAdDetails(data));
            return card;
        } catch (IOException e) { showError("خطا در نمایش آگهی"); return null; }
    }

    private void openAdDetails(AdData data) {
        adService.fetchAdDetails(data.id(), detail -> Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdDetails.fxml"));
                Parent root = loader.load();
                loader.<AdDetailsController>getController().setData(detail, adService, "ADMIN", false, this::refreshAll, viewManager);
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
            if (category) adminService.updateCategory(id, name, c -> loadCategories(), this::showError);
            else adminService.updateCity(id, name, c -> loadCities(), this::showError);
        });
    }

    private void styleDialog(Dialog<?> dialog) {
        dialog.initOwner(statusLabel.getScene().getWindow());
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/Dialog.css").toExternalForm());
        dialog.getDialogPane().setNodeOrientation(javafx.geometry.NodeOrientation.RIGHT_TO_LEFT);
    }

    private void deleteMetadata(boolean category, Long id) {
        if (category) adminService.deleteCategory(id, ok -> { loadCategories(); loadStats(); }, this::showError);
        else adminService.deleteCity(id, ok -> { loadCities(); loadStats(); }, this::showError);
    }

    private void showSuccess(String message) { statusLabel.setText(message); statusLabel.setStyle("-fx-text-fill: #4ade80;"); }
    private void showError(String message) { statusLabel.setText("خطا: " + message); statusLabel.setStyle("-fx-text-fill: #ff5a5a;"); }
    @FXML private void goBack() { viewManager.toMain(); }
}
