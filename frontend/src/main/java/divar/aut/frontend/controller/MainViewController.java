package divar.aut.frontend.controller;

import divar.aut.frontend.net.AdService;
import divar.aut.frontend.net.CategoryService;
import divar.aut.frontend.net.CityService;
import divar.aut.frontend.model.AdData;
import divar.aut.frontend.model.CategoryData;
import divar.aut.frontend.model.CityData;
import divar.aut.frontend.ui.ViewManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.animation.*;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    @FXML private FlowPane         adGrid;
    @FXML private ScrollPane       adScrollPane;
    @FXML private TextField        searchField;
    @FXML private ComboBox<String> sortCombo;
    @FXML private Button           loadMoreBtn;
    @FXML private Label            statusLabel;
    @FXML private Button           myAdsBtn;
    @FXML private Button           adminPanelBtn;
    @FXML private ComboBox<String> cityCombo;
    @FXML private ComboBox<String> conditionCombo;
    @FXML private TextField        minPriceField;
    @FXML private TextField        maxPriceField;

    private AdService adService;
    private final CategoryService categoryService = new CategoryService();
    private final CityService cityService = new CityService();
    private final List<CategoryData> categories = new ArrayList<>();
    private final List<CityData> cities = new ArrayList<>();
    private Long selectedCategoryId;
    private final ViewManager viewManager;
    private int page = 0;
    private boolean showingMyAds = false;

    /** No-arg constructor required by FXMLLoader — do NOT remove */
    public MainViewController() { this.viewManager = null; }

    /** Called by MainView via controller factory */
    public MainViewController(ViewManager vm) { this.viewManager = vm;}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (sortCombo != null) {
            sortCombo.getItems().addAll("جدیدترین", "ارزان‌ترین", "گران‌ترین");
            sortCombo.getSelectionModel().selectFirst();
            sortCombo.setOnAction(e -> applyFilter());
        }
        if (conditionCombo != null) {
            conditionCombo.getItems().addAll("همه", "نو", "کارکرده");
            conditionCombo.getSelectionModel().selectFirst();
        }
        if (cityCombo != null) {
            cityCombo.getItems().add("همه شهرها");
            cityCombo.getSelectionModel().selectFirst();
        }
        if (viewManager != null && viewManager.getUserToken() != null) {
            this.adService = new AdService();
            loadFilterOptions();
            loadPage();
        } else {
            if (statusLabel != null) statusLabel.setText("خطا: توکن احراز هویت یافت نشد!");
        }
        //if user is not ADMIN we won't show AdminPanelButton
        if (viewManager != null && !"ADMIN".equals(viewManager.getUserRole())) {
            adminPanelBtn.setVisible(false);
            adminPanelBtn.setManaged(false);
        }
    }

    private void loadPage() {
        if (statusLabel != null) statusLabel.setText(showingMyAds ? "در حال دریافت آگهی‌های من..." : "در حال دریافت آگهی‌ها...");
        if (loadMoreBtn != null) {
            loadMoreBtn.setDisable(true);
            loadMoreBtn.setVisible(!showingMyAds);
            loadMoreBtn.setManaged(!showingMyAds);
        }

        if (showingMyAds) {
            adService.fetchMyAds(
                    ads -> {
                        if (statusLabel != null) statusLabel.setText(ads.size() + " آگهی دریافت شد");
                        if (loadMoreBtn != null) loadMoreBtn.setDisable(true);
                        renderAds(ads);
                    },
                    error -> {
                        if (statusLabel != null) statusLabel.setText("خطا در ارتباط با سرور: " + error);
                        if (loadMoreBtn != null) loadMoreBtn.setDisable(false);
                    }
            );
        } else {
            adService.searchAds(searchText(), selectedCategoryId, selectedCityId(), parsePrice(minPriceField),
                    parsePrice(maxPriceField), selectedCondition(), selectedSort(),
                    ads -> {
                        if (statusLabel != null) statusLabel.setText(ads.size() + " آگهی دریافت شد");
                        if (ads.isEmpty()) {
                            if (loadMoreBtn != null) {
                                loadMoreBtn.setDisable(true);
                                loadMoreBtn.setText("پایان آگهی‌ها");
                            }
                        } else {
                            if (loadMoreBtn != null) {
                                loadMoreBtn.setDisable(false);
                                loadMoreBtn.setText("نمایش بیشتر");
                            }
                            renderAds(ads);
                        }
                    },
                    error -> {
                        if (statusLabel != null) statusLabel.setText("خطا در ارتباط با سرور: " + error);
                        if (loadMoreBtn != null) loadMoreBtn.setDisable(false);
                    }
            );
        }
    }
    public void renderAds(List<AdData> ads) {
        adGrid.getChildren().clear();
        int delay = 0;
        for (AdData ad : ads) {
            Node card = buildCard(ad);
            if (card == null) continue;

            card.setOpacity(0);
            adGrid.getChildren().add(card);

            FadeTransition ft = new FadeTransition(Duration.millis(280), card);
            ft.setToValue(1);
            ft.setDelay(Duration.millis(delay));
            ft.play();
            delay += 50;
        }
    }

    private Node buildCard(AdData data) {
        try {
            URL cardUrl = getClass().getResource("/AdCard.fxml");
            if (cardUrl == null) {
                System.err.println("❌ AdCard.fxml not found on classpath");
                return null;
            }
            FXMLLoader loader = new FXMLLoader(cardUrl);
            Parent card = loader.load();

            AdCardController ctrl = loader.getController();
            ctrl.setData(data);

            // hover روی root node بعد از load
            card.setOnMouseEntered(e -> {
                card.setScaleX(1.03);
                card.setScaleY(1.03);
                card.setStyle("-fx-effect:dropshadow(gaussian,rgba(239,63,63,0.3),24,0.1,0,6);");
            });
            card.setOnMouseExited(e -> {
                card.setScaleX(1.0);
                card.setScaleY(1.0);
                card.setStyle("");
            });
            card.setOnMouseClicked(e -> {
                if (adService == null || viewManager == null) return;
                adService.fetchAdDetails(data.id(), adDetail -> {
                    Platform.runLater(() -> openAdDetails(adDetail));
                }, error -> {
                    Platform.runLater(() -> {
                        if (statusLabel != null) statusLabel.setText("خطا در دریافت جزئیات آگهی: " + error);
                    });
                });
            });
            return card;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void loadMore() {
        page++;
        loadPage();
    }

    private void openAdDetails(divar.aut.frontend.model.AdDetailData adDetail) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdDetails.fxml"));
            Parent root = loader.load();
            AdDetailsController controller = loader.getController();
            controller.setData(adDetail, adService, viewManager.getUserRole(), showingMyAds, () -> {
                page = 0;
                adGrid.getChildren().clear();
                loadPage();
            }, viewManager);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("جزئیات آگهی: " + adDetail.title());
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            if (statusLabel != null) statusLabel.setText("خطا در باز کردن جزئیات آگهی");
        }
    }

    @FXML
    private void onMyAds() {
        if (viewManager == null || viewManager.getUserToken() == null) return;
        showingMyAds = !showingMyAds;
        if (myAdsBtn != null) {
            myAdsBtn.setText(showingMyAds ? "بازگشت به آگهی‌ها" : "دیوار من");
        }
        page = 0;
        adGrid.getChildren().clear();
        loadPage();
    }

    @FXML private void onPostAd() {
        if(viewManager == null || viewManager.getUserToken() == null) return;
        viewManager.toPostAd();
    }

    @FXML private void onFavorites() {
        if(viewManager == null || viewManager.getUserToken() == null) return;
        viewManager.toFavorites();
    }

    @FXML private void filterCategory(javafx.event.ActionEvent e) {
        String categoryName = ((Button) e.getSource()).getText();
        selectedCategoryId = categories.stream()
                .filter(category -> category.name().equals(categoryName))
                .map(CategoryData::id)
                .findFirst()
                .orElse(null);
        if ("همه".equals(categoryName)) {
            selectedCategoryId = null;
        }
        applyFilter();
    }

    @FXML private void applyFilter() {
        if (showingMyAds) {
            showingMyAds = false;
            if (myAdsBtn != null) myAdsBtn.setText("دیوار من");
        }
        page = 0;
        loadPage();
    }

    private void loadFilterOptions() {
        categoryService.listAll(
                loaded -> {
                    categories.clear();
                    categories.addAll(loaded);
                },
                error -> {
                    if (statusLabel != null) statusLabel.setText("خطا در دریافت دسته‌بندی‌ها: " + error);
                }
        );
        cityService.listAll(
                loaded -> {
                    cities.clear();
                    cities.addAll(loaded);
                    if (cityCombo != null) {
                        cityCombo.getItems().setAll("همه شهرها");
                        cityCombo.getItems().addAll(loaded.stream().map(CityData::name).toList());
                        cityCombo.getSelectionModel().selectFirst();
                    }
                },
                error -> {
                    if (statusLabel != null) statusLabel.setText("خطا در دریافت شهرها: " + error);
                }
        );
    }

    private String searchText() {
        return searchField == null ? null : searchField.getText();
    }

    private Long selectedCityId() {
        if (cityCombo == null || cityCombo.getValue() == null || "همه شهرها".equals(cityCombo.getValue())) {
            return null;
        }
        return cities.stream()
                .filter(city -> city.name().equals(cityCombo.getValue()))
                .map(CityData::id)
                .findFirst()
                .orElse(null);
    }

    private Double parsePrice(TextField field) {
        if (field == null || field.getText() == null || field.getText().isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(field.getText().trim());
        } catch (NumberFormatException e) {
            if (statusLabel != null) statusLabel.setText("قیمت باید عدد باشد");
            return null;
        }
    }

    private String selectedCondition() {
        if (conditionCombo == null || conditionCombo.getValue() == null || "همه".equals(conditionCombo.getValue())) {
            return null;
        }
        return switch (conditionCombo.getValue()) {
            case "نو" -> "NEW";
            default -> "USED";
        };
    }

    private String selectedSort() {
        if (sortCombo == null || sortCombo.getValue() == null) {
            return "newest";
        }
        return switch (sortCombo.getValue()) {
            case "ارزان‌ترین" -> "cheapest";
            case "گران‌ترین" -> "expensive";
            default -> "newest";
        };
    }
    @FXML
    private void onAdminPanel() {
        if(viewManager == null || viewManager.getUserToken() == null) return;
        viewManager.toAdminDashboard();
    }
}
