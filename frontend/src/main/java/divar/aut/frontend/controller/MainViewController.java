package divar.aut.frontend.controller;

import divar.aut.frontend.SessionManager;
import divar.aut.frontend.net.AdService;
import divar.aut.frontend.net.CategoryService;
import divar.aut.frontend.net.CityService;
import divar.aut.frontend.net.ConversationService;
import divar.aut.frontend.model.AdData;
import divar.aut.frontend.model.CategoryData;
import divar.aut.frontend.model.CityData;
import divar.aut.frontend.ui.ViewManager;
import divar.aut.frontend.util.PriceFormatter;
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

/**
 * JavaFX controller for the main application screen.
 * <p>
 * This is the central view after login, displaying advertisements in a grid
 * with filtering and sorting capabilities. It also provides navigation to
 * other sections: "My Ads", "Post Ad", "Favorites", "Conversations",
 * and "Admin Panel" (for admins). The view supports theme switching and
 * includes a chat unread‑count badge. It interacts with multiple backend
 * services ({@link AdService}, {@link CategoryService}, {@link CityService},
 * and {@link ConversationService}) to fetch data.
 * </p>
 */
public class MainViewController implements Initializable {

    @FXML private FlowPane         adGrid;
    @FXML private ScrollPane       adScrollPane;
    @FXML private TextField        searchField;
    @FXML private ComboBox<String> sortCombo;
    @FXML private Label            statusLabel;
    @FXML private Button           myAdsBtn;
    @FXML private Label            sectionTitleLabel;
    @FXML private Button           adminPanelBtn;
    @FXML private Button           themeToggleBtn;
    @FXML private HBox             categoryBar;
    @FXML private ComboBox<String> cityCombo;
    @FXML private ComboBox<String> conditionCombo;
    @FXML private TextField        minPriceField;
    @FXML private TextField        maxPriceField;
    @FXML private Label            chatBadge;

    private AdService adService;
    private final CategoryService categoryService = new CategoryService();
    private final CityService cityService = new CityService();
    private final ConversationService conversationService = new ConversationService();
    private final List<CategoryData> categories = new ArrayList<>();
    private final List<CityData> cities = new ArrayList<>();
    private Long selectedCategoryId;
    private final ViewManager viewManager;
    private int page = 0;
    private boolean showingMyAds = false;

    /**
     * No‑arg constructor required by FXMLLoader. Do not remove.
     */
    public MainViewController() { this.viewManager = null; }

    /**
     * Constructor used by MainView via a controller factory.
     *
     * @param vm the injected view manager for navigation and state.
     */
    public MainViewController(ViewManager vm) { this.viewManager = vm;}

    /**
     * Initializes the controller after the FXML is loaded.
     * <p>
     * Configures sort and filter combo boxes, loads filter options (categories,
     * cities), loads the initial page of ads, refreshes the chat badge, and
     * hides the admin panel button if the user is not an admin.
     * </p>
     *
     * @param location  the location used to resolve relative paths (unused).
     * @param resources the resources used to localize the root object (unused).
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (sortCombo != null) {
            sortCombo.getItems().addAll("جدیدترین", "ارزان‌ترین", "گران‌ترین", "بیشترین امتیاز");
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
        if (minPriceField != null) PriceFormatter.attachLiveGrouping(minPriceField);
        if (maxPriceField != null) PriceFormatter.attachLiveGrouping(maxPriceField);
        if (viewManager != null && viewManager.getUserToken() != null) {
            this.adService = new AdService();
            loadFilterOptions();
            loadPage();
            refreshChatBadge();
        } else {
            if (statusLabel != null) statusLabel.setText("خطا: توکن احراز هویت یافت نشد!");
        }
        //if user is not ADMIN we won't show AdminPanelButton
        if (viewManager != null && !"ADMIN".equals(viewManager.getUserRole())) {
            adminPanelBtn.setVisible(false);
            adminPanelBtn.setManaged(false);
        }
        divar.aut.frontend.ui.ThemeManager.syncButtonLabel(themeToggleBtn);
    }

    private void loadPage() {
        if (statusLabel != null) statusLabel.setText(showingMyAds ? "در حال دریافت آگهی‌های من..." : "در حال دریافت آگهی‌ها...");

        if (showingMyAds) {
            adService.fetchMyAds(
                    ads -> {
                        if (statusLabel != null) statusLabel.setText(ads.size() + " آگهی دریافت شد");
                        renderAds(ads);
                    },
                    error -> {
                        if (statusLabel != null) statusLabel.setText("خطا در ارتباط با سرور: " + error);
                    }
            );
        } else {
            adService.searchAds(searchText(), selectedCategoryId, selectedCityId(), parsePrice(minPriceField),
                    parsePrice(maxPriceField), selectedCondition(), selectedSort(),
                    ads -> {
                        if (statusLabel != null) statusLabel.setText(ads.size() + " آگهی دریافت شد");
                        renderAds(ads);
                    },
                    error -> {
                        if (statusLabel != null) statusLabel.setText("خطا در ارتباط با سرور: " + error);
                    }
            );
        }
    }

    /**
     * Renders a list of advertisements in the grid with a fade-in animation.
     * Clears the existing grid and adds each ad card with a staggered delay.
     *
     * @param ads the list of ad data to display.
     */
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
                card.setStyle("-fx-effect:dropshadow(gaussian,rgba(76,125,255,0.35),24,0.1,0,6);");
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

    private void openAdDetails(divar.aut.frontend.model.AdDetailData adDetail) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdDetails.fxml"));
            Parent root = loader.load();
            AdDetailsController controller = loader.getController();
            Runnable backAction = showingMyAds ? () -> viewManager.toMain(true) : viewManager::toMain;
            controller.setData(adDetail, adService, viewManager.getUserRole(), showingMyAds, () -> {
                page = 0;
                adGrid.getChildren().clear();
                loadPage();
            }, viewManager, null, backAction);
            viewManager.show(root);
        } catch (IOException ex) {
            ex.printStackTrace();
            if (statusLabel != null) statusLabel.setText("خطا در باز کردن جزئیات آگهی");
        }
    }

    /**
     * Toggles between showing "My Ads" and the main ad list.
     */
    @FXML
    private void onMyAds() {
        if (viewManager == null || viewManager.getUserToken() == null) return;
        showingMyAds = !showingMyAds;
        if (myAdsBtn != null) {
            myAdsBtn.setText(showingMyAds ? "بازگشت به آگهی‌ها" : "دیوار من");
        }
        if (sectionTitleLabel != null) {
            sectionTitleLabel.setText(showingMyAds ? "آگهی‌های شما" : "انواع آگهی‌ها و نیازمندی‌ها");
        }
        page = 0;
        adGrid.getChildren().clear();
        loadPage();
    }

    public void showMyAdsTab() {
        if (!showingMyAds) {
            showingMyAds = true;
            if (myAdsBtn != null) myAdsBtn.setText("بازگشت به آگهی‌ها");
            if (sectionTitleLabel != null) sectionTitleLabel.setText("آگهی‌های شما");
            page = 0;
            adGrid.getChildren().clear();
            loadPage();
        }
    }

    public void showAllAdsTab() {
        if (showingMyAds) {
            showingMyAds = false;
            if (myAdsBtn != null) myAdsBtn.setText("دیوار من");
            if (sectionTitleLabel != null) sectionTitleLabel.setText("انواع آگهی‌ها و نیازمندی‌ها");
        }
        page = 0;
        adGrid.getChildren().clear();
        loadPage();
    }

    /**
     * Navigates to the "Post Ad" screen.
     */
    @FXML private void onPostAd() {
        if(viewManager == null || viewManager.getUserToken() == null) return;
        viewManager.toPostAd();
    }

    /**
     * Navigates to the "Favorites" screen.
     */
    @FXML private void onFavorites() {
        if(viewManager == null || viewManager.getUserToken() == null) return;
        viewManager.toFavorites();
    }

    /**
     * Navigates to the "Conversations" screen.
     */
    @FXML private void onConversations() {
        if(viewManager == null || viewManager.getUserToken() == null) return;
        viewManager.toConversations();
    }

    /**
     * Re‑fetches conversations and updates the unread‑count badge on the "گفتگوها" nav button.
     * <p>
     * This method is safe to call whenever the main view regains focus (e.g., after closing the
     * conversations or a conversation‑detail window) so the badge count does not become stale.
     * </p>
     */
    public void refreshChatBadge() {
        if (chatBadge == null || viewManager == null || viewManager.getUserToken() == null) return;
        conversationService.listConversations(
                conversations -> {
                    int total = conversations.stream().mapToInt(divar.aut.frontend.model.ConversationData::unreadCount).sum();
                    Platform.runLater(() -> {
                        if (total > 0) {
                            chatBadge.setText(total > 99 ? "99+" : String.valueOf(total));
                            chatBadge.setVisible(true);
                            chatBadge.setManaged(true);
                        } else {
                            chatBadge.setVisible(false);
                            chatBadge.setManaged(false);
                        }
                    });
                },
                error -> { /* badge is non-critical; fail silently */ }
        );
    }

    private void selectCategory(Long categoryId) {
        selectedCategoryId = categoryId;
        applyFilter();
    }

    private void renderCategoryButtons() {
        if (categoryBar == null) return;
        categoryBar.getChildren().removeIf(node -> node instanceof Button);
        categoryBar.getChildren().add(categoryButton("همه", null));
        categories.forEach(category ->
                categoryBar.getChildren().add(categoryButton(category.name(), category.id())));
    }

    private Button categoryButton(String name, Long categoryId) {
        Button button = new Button(name);
        button.getStyleClass().add("cat-chip");
        button.setOnAction(event -> selectCategory(categoryId));
        return button;
    }

    /**
     * Applies the current filter and sorting criteria and reloads the ad list.
     * Also resets the "My Ads" toggle if it was active.
     */
    @FXML private void applyFilter() {
        if (showingMyAds) {
            showingMyAds = false;
            if (myAdsBtn != null) myAdsBtn.setText("دیوار من");
            if (sectionTitleLabel != null) sectionTitleLabel.setText("انواع آگهی‌ها و نیازمندی‌ها");
        }
        page = 0;
        loadPage();
    }

    private void loadFilterOptions() {
        categoryService.listAll(
                loaded -> {
                    categories.clear();
                    categories.addAll(loaded);
                    if (selectedCategoryId != null && categories.stream()
                            .noneMatch(category -> category.id().equals(selectedCategoryId))) {
                        selectedCategoryId = null;
                    }
                    renderCategoryButtons();
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
            return PriceFormatter.parse(field.getText());
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
            case "بیشترین امتیاز" -> "highest_rating";
            default -> "newest";
        };
    }

    /**
     * Navigates to the admin dashboard.
     * Only visible if the current user has the ADMIN role.
     */
    @FXML
    private void onAdminPanel() {
        if(viewManager == null || viewManager.getUserToken() == null) return;
        viewManager.toAdminDashboard();
    }

    /**
     * Logs out the current user and returns to the welcome screen.
     */
    @FXML
    private void onLogout() {
        if (viewManager == null) return;
        SessionManager.getInstance().endSession();
        viewManager.toWelcome();
    }

    /**
     * Toggles the application theme (light/dark) via {@code ThemeManager}.
     */
    @FXML
    private void onToggleTheme() {
        if (viewManager == null) return;
        viewManager.toggleTheme();
        divar.aut.frontend.ui.ThemeManager.syncButtonLabel(themeToggleBtn);
    }
}