package divar.aut.frontend.controller;

import divar.aut.frontend.model.AdData;
import divar.aut.frontend.util.PriceFormatter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * JavaFX controller for rendering an individual advertisement card in the UI.
 * <p>
 * Binds to an FXML layout that displays ad image, title, price, location,
 * time, photo count, and item condition badge. The card is populated via
 * the {@link #setData(AdData)} method.
 * </p>
 */
public class AdCardController implements Initializable {

    @FXML private VBox       cardRoot;
    @FXML private ImageView  adImage;
    @FXML private Label      adTitle;
    @FXML private Label      adPrice;
    @FXML private Label      adLocation;
    @FXML private Label      adTime;
    @FXML private Label      photoCount;
    @FXML private Label      conditionLabel;

    /**
     * Initializes the controller after its root element has been fully processed.
     * <p>
     * The card is reused inside screens that don't necessarily carry
     * {@code theme.css} themselves (e.g. the admin dashboard, which uses
     * {@code AdminDashboard.css}), so the card attaches its own base
     * stylesheet here and then asks {@link divar.aut.frontend.ui.ThemeManager}
     * to layer the light variant on top when needed. Because both live on
     * this same node's stylesheet list, the light variant reliably wins the
     * cascade tie-break over the dark base rules, regardless of what the
     * parent screen is doing with its own stylesheets.
     * </p>
     *
     * @param location  the location used to resolve relative paths (unused)
     * @param resources the resources used to localize the root object (unused)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cardRoot.getStylesheets().add(getClass().getResource("/theme.css").toExternalForm());
        divar.aut.frontend.ui.ThemeManager.applyCurrentMode(cardRoot);
    }

    /**
     * Populates the card UI with data from an {@link AdData} object.
     * <p>
     * Sets the title, price, location, time, category (as photo count placeholder),
     * and condition badge. Also attempts to load the thumbnail image from the
     * server. If the thumbnail fails to load, the error is logged and the image
     * remains empty.
     * </p>
     *
     * @param data the ad data to display; must not be null.
     */
    public void setData(AdData data) {
        adTitle.setText(data.title());
        adPrice.setText(PriceFormatter.format(data.price()));
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
                Image image = new Image(fullUrl, true);
                adImage.setImage(image);
                applyCenterCropViewport(image);
            } catch (Exception e) {
                System.err.println("Failed to load image for ad: " + data.title());
                e.printStackTrace();
            }
        }
    }

    /**
     * Crops the image to the center of its frame instead of letting
     * {@link ImageView} stretch it to fill {@code adImage}'s fit box. Only
     * the display is cropped (via the view's viewport) — the underlying
     * image file is left untouched.
     */
    private void applyCenterCropViewport(Image image) {
        if (image.getWidth() > 0 && image.getHeight() > 0) {
            setCenterCropViewport(image);
        } else {
            image.heightProperty().addListener((obs, oldHeight, newHeight) -> {
                if (newHeight.doubleValue() > 0 && image.getWidth() > 0) {
                    setCenterCropViewport(image);
                }
            });
        }
    }

    private void setCenterCropViewport(Image image) {
        double targetRatio = adImage.getFitWidth() / adImage.getFitHeight();
        double imgWidth = image.getWidth();
        double imgHeight = image.getHeight();
        double imgRatio = imgWidth / imgHeight;

        double cropWidth;
        double cropHeight;
        if (imgRatio > targetRatio) {
            cropHeight = imgHeight;
            cropWidth = imgHeight * targetRatio;
        } else {
            cropWidth = imgWidth;
            cropHeight = imgWidth / targetRatio;
        }

        double x = (imgWidth - cropWidth) / 2.0;
        double y = (imgHeight - cropHeight) / 2.0;
        adImage.setViewport(new Rectangle2D(x, y, cropWidth, cropHeight));
    }
}