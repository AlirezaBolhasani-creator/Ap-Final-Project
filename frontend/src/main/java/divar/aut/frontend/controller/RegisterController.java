package divar.aut.frontend.controller;

import divar.aut.frontend.net.AuthService;
import divar.aut.frontend.ui.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {
    @FXML private TextField nameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private Label statusLabel;

    private ViewManager viewManager;

    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    @FXML
    private void handleRegister() {
        statusLabel.getStyleClass().removeAll("status-success", "status-danger");
        statusLabel.getStyleClass().add("status-danger");

        AuthService.sendRegisterRequest(
                nameField.getText(),
                usernameField.getText(),
                passwordField.getText(),
                emailField.getText(),
                phoneField.getText(),
                (token, role) -> {
                    viewManager.setUserToken(token);
                    viewManager.setUserRole(role);
                    statusLabel.getStyleClass().remove("status-danger");
                    statusLabel.getStyleClass().add("status-success");
                    statusLabel.setText("ثبت نام موفقیت‌آمیز بود");
                    viewManager.toMain();
                },
                error -> statusLabel.setText("خطا: " + error));
    }

    @FXML private void goWelcome() { if (viewManager != null) viewManager.toWelcome(); }
}
