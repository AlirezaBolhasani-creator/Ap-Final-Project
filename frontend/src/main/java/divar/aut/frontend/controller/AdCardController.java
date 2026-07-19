package divar.aut.frontend.controller;

import divar.aut.frontend.model.AdData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class AdCardController implements Initializable {

    @FXML private ImageView  adImage;
    @FXML private Label      adTitle;
    @FXML private Label      adPrice;
    @FXML private Label      adLocation;
    @FXML private Label      adTime;
    @FXML private Label      photoCount;
    @FXML private Label      conditionLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setData(AdData data) {
        adTitle.setText(data.title());
        adPrice.setText(data.price() == null ? "0" : String.valueOf(data.price()));
        adLocation.setText(data.cityName() != null ? data.cityName() : "ناشناس");
        adTime.setText(data.status() != null ? data.status() : "-" );
        photoCount.setText(data.categoryName() != null ? data.categoryName() : "");

        if (data.itemCondition() != null && !data.itemCondition().isBlank()) {
            String labelText = switch (data.itemCondition()) {
                case "NEW" -> "نو";
                case "USED" -> "کارکرده";
                default -> data.itemCondition();
            };
            conditionLabel.setText(labelText);
            conditionLabel.setVisible(true);
            String badgeClass = switch (labelText) {
                case "نو" -> "badge-success";
                case "کارکرده" -> "badge-warning";
                default -> "badge-info";
            };
            conditionLabel.getStyleClass().setAll("badge", badgeClass, "ad-card-obadge");
        } else {
            conditionLabel.setVisible(false);
        }

        if (data.thumbnailFileName() != null && !data.thumbnailFileName().isBlank()) {
            try {
                String fullUrl = "http://localhost:8080/uploads/" + data.thumbnailFileName();
                fullUrl = fullUrl.replace(" ", "%20");
                adImage.setImage(new Image(fullUrl, true));
            } catch (Exception e) {
                System.err.println("Failed to load image for ad: " + data.title());
                e.printStackTrace();
            }
        }
    }
}



