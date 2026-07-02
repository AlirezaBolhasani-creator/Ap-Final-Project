package divar.aut.frontend.controller;
import divar.aut.frontend.service.AdService;
import divar.aut.frontend.model.AdData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
public class PostAdController implements Initializable
{
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField priceField;
    @FXML private TextField locationField;
    @FXML private ComboBox<String> conditionCombo;
    @FXML private StackPane imageUploadBox;
    @FXML private Label statusLabel;

    private AdService adService;
    private File selectedImageFile = null;

    public void setAdService(AdService adService)
    {
        this.adService = adService;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        if(conditionCombo !=null)
        {
            conditionCombo.getItems().addAll("نو", "در حد نو", "کارکرده");
        }
    }
    @FXML
    public void handleImageUpload()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("انتخاب عکس");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(imageUploadBox.getScene().getWindow());
        if(file!=null)
        {
            selectedImageFile = file;
            statusLabel.setText("عکس انتخاب شد: " + file.getName());
            statusLabel.setStyle("-fx-text-fill: #4CAF50;");
        }
    }
    @FXML
    public void handleClear()
    {
        titleField.clear();
        descriptionArea.clear();
        priceField.clear();
        locationField.clear();
        if(conditionCombo!=null)
        {
            conditionCombo.getSelectionModel().clearSelection();
        }
        selectedImageFile = null;
        statusLabel.setText("");
    }
    @FXML
    public void handleSubmit() {
        Double priceValue = 0.0;
        try {
            priceValue = Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            statusLabel.setText("قیمت باید عدد باشد!");
            return;
        }

        // (title, price, location, condition, imageUrl, photoCount, id)
        AdData newAd = new AdData(
                titleField.getText(),
                Double.parseDouble(priceField.getText()),
                locationField.getText(),
                conditionCombo.getValue(),
                null, // imageUrl
                0,    // photoCount
                null  // id
        );

        // چاپ برای اطمینان قبل از ارسال
        System.out.println("JSON Payload to send: " + new com.google.gson.Gson().toJson(newAd));

        adService.createAd(newAd,
                createdAd -> {
                    // منطق آپلود عکس (همان‌طور که قبلاً نوشتیم)
                    if (selectedImageFile != null) {
                        adService.uploadImage(createdAd.id(), selectedImageFile,
                                msg -> statusLabel.setText("آگهی با عکس ثبت شد!"),
                                err -> statusLabel.setText("عکس خطا خورد: " + err)
                        );
                    } else {
                        statusLabel.setText("آگهی بدون عکس ثبت شد!");
                    }
                },
                error -> statusLabel.setText("خطا در ثبت: " + error)
        );
    }
}
