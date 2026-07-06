package divar.aut.frontend.controller;

import divar.aut.frontend.model.AdData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class AdCardController implements Initializable {

    @FXML private Rectangle  glassRect;
    @FXML private Rectangle  borderRect;
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
            String chipStyle = switch (labelText) {
                case "نو" -> "-fx-background-color:rgba(74,222,128,.18);-fx-text-fill:#4ade80;-fx-border-color:rgba(74,222,128,.3);";
                case "کارکرده" -> "-fx-background-color:rgba(168,85,247,.18);-fx-text-fill:#a855f7;-fx-border-color:rgba(168,85,247,.3);";
                default -> "-fx-background-color:rgba(96,165,250,.18);-fx-text-fill:#60a5fa;-fx-border-color:rgba(96,165,250,.3);";
            };
            conditionLabel.setStyle(chipStyle +
                    "-fx-padding:3 8 3 8;-fx-background-radius:12;-fx-border-radius:12;-fx-border-width:1;");
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



