package divar.aut.frontend.controller;

import divar.aut.frontend.net.AuthService;
import divar.aut.frontend.ui.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * JavaFX controller for the login screen.
 * Handles user authentication by sending credentials to the server,
 * and on success stores the token and role in {@link ViewManager}
 * before navigating to the main screen.
 */
public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private ViewManager viewManager;

    /**
     * Injects the view manager for navigation.
     *
     * @param viewManager the navigation manager.
     */
    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    /**
     * Handles the login button action.
     * Reads the username and password, sends a login request via
     * {@link AuthService}, and updates the view accordingly.
     * On success, stores the token and role and navigates to the main screen.
     * On error, displays the error message in the status label.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        statusLabel.getStyleClass().removeAll("status-success", "status-danger");
        statusLabel.getStyleClass().add("status-danger");

        AuthService.sendAuthRequest(username, password, "/login",
                (token, role) -> {
                    viewManager.setUserToken(token);
                    viewManager.setUserRole(role);
                    statusLabel.getStyleClass().remove("status-danger");
                    statusLabel.getStyleClass().add("status-success");
                    statusLabel.setText("ورود موفقیت‌آمیز بود");
                    viewManager.toMain();
                },
                error -> statusLabel.setText("خطا: " + error));
    }

    /**
     * Navigates back to the welcome screen.
     */
    @FXML private void goWelcome() { if (viewManager != null) viewManager.toWelcome(); }
}