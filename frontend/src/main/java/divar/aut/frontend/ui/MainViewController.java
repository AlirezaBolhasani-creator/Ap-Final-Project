package divar.aut.frontend.ui;

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

    private final ViewManager viewManager;
    private int page = 0;

    /** No-arg constructor required by FXMLLoader — do NOT remove */
    public MainViewController() { this.viewManager = null; }

    /** Called by MainView via controller factory */
    public MainViewController(ViewManager vm) { this.viewManager = vm; }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (sortCombo != null) {
            sortCombo.getItems().addAll("جدیدترین", "ارزان‌ترین", "گران‌ترین");
            sortCombo.getSelectionModel().selectFirst();
        }
        loadPage();
    }

    private void loadPage() {
        List<AdData> ads = AdData.samplePage(page);
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
        if (statusLabel != null)
            statusLabel.setText(adGrid.getChildren().size() + " آگهی نمایش داده می‌شود");
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
        loadMoreBtn.setDisable(true);
        PauseTransition pause = new PauseTransition(Duration.millis(600));
        pause.setOnFinished(e -> loadMoreBtn.setDisable(false));
        pause.play();
    }

    @FXML private void onPostAd() {
        statusLabel.setText("در حال انتقال به صفحه ثبت آگهی…");
    }

    @FXML private void filterCategory(javafx.event.ActionEvent e) {
        statusLabel.setText("فیلتر: " + ((Button) e.getSource()).getText());
    }

    @FXML private void applyFilter() {
        statusLabel.setText("فیلتر قیمت اعمال شد");
    }
}
