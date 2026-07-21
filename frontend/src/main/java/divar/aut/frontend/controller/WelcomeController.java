package divar.aut.frontend.controller;

import divar.aut.frontend.ui.ThemeManager;
import divar.aut.frontend.ui.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class WelcomeController {
    private ViewManager viewManager;

    @FXML private Button themeToggleBtn;

    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    @FXML
    private void initialize() {
        ThemeManager.syncButtonLabel(themeToggleBtn);
    }

    @FXML private void goLogin() { if (viewManager != null) viewManager.toLogin(); }
    @FXML private void goRegister() { if (viewManager != null) viewManager.toRegister(); }

    @FXML
    private void onToggleTheme() {
        if (viewManager == null) return;
        viewManager.toggleTheme();
        ThemeManager.syncButtonLabel(themeToggleBtn);
    }
}
