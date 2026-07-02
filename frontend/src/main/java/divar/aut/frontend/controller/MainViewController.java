package divar.aut.frontend.controller;

import divar.aut.frontend.service.AdService;
import divar.aut.frontend.model.AdData;
import divar.aut.frontend.ui.ViewManager;
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
import java.util.List;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    @FXML private FlowPane         adGrid;
    @FXML private ScrollPane       adScrollPane;
    @FXML private TextField        searchField;
    @FXML private ComboBox<String> sortCombo;
    @FXML private Button           loadMoreBtn;
    @FXML private Label            statusLabel;

    private AdService adService;
    private final ViewManager viewManager;
    private int page = 0;

    /** No-arg constructor required by FXMLLoader — do NOT remove */
    public MainViewController() { this.viewManager = null; }

    /** Called by MainView via controller factory */
    public MainViewController(ViewManager vm) { this.viewManager = vm;}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (sortCombo != null) {
            sortCombo.getItems().addAll("جدیدترین", "ارزان‌ترین", "گران‌ترین");
            sortCombo.getSelectionModel().selectFirst();
        }
        if (viewManager != null && viewManager.getUserToken() != null) {
            this.adService = new AdService(viewManager.getUserToken());
            loadPage();
        } else {
            if (statusLabel != null) statusLabel.setText("خطا: توکن احراز هویت یافت نشد!");
        }
    }

    private void loadPage() {
        if (statusLabel != null) statusLabel.setText("در حال دریافت آگهی‌ها...");
        if (loadMoreBtn != null) loadMoreBtn.setDisable(true);
        adService.fetchAds(page,
                ads -> {
                    if (statusLabel != null) statusLabel.setText(ads.size() + " آگهی دریافت شد");

                    // NEW LOGIC: Check if we've reached the end of the ads
                    if (ads.isEmpty()) {
                        if (loadMoreBtn != null) {
                            loadMoreBtn.setDisable(true);
                            loadMoreBtn.setText("پایان آگهی‌ها"); // Updates button text so the user knows
                        }
                    } else {
                        if (loadMoreBtn != null) loadMoreBtn.setDisable(false);
                        renderAds(ads);
                    }
                },
                error -> {
                    if (statusLabel != null) statusLabel.setText("خطا در ارتباط با سرور: " + error);
                    if (loadMoreBtn != null) loadMoreBtn.setDisable(false);
                }
        );
    }
    public void renderAds(List<AdData> ads) {
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

    @FXML private void onPostAd() {
        if(viewManager == null || viewManager.getUserToken() == null) return;
        viewManager.toPostAd();
    }

    @FXML private void filterCategory(javafx.event.ActionEvent e) {
        statusLabel.setText("فیلتر: " + ((Button) e.getSource()).getText());
    }

    @FXML private void applyFilter() {
        statusLabel.setText("فیلتر قیمت اعمال شد");
    }
}
