package divar.aut.frontend.controller;

import divar.aut.frontend.ui.ThemeManager;
import divar.aut.frontend.ui.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * JavaFX controller for the welcome screen.
 * <p>
 * This is the landing page presented to unauthenticated users. It provides
 * buttons to navigate to the login or registration screens, and a theme
 * toggle button that switches between light and dark modes.
 * </p>
 */
public class WelcomeController {
    private ViewManager viewManager;

    @FXML private Button themeToggleBtn;

    /**
     * Injects the view manager for navigation and theme control.
     *
     * @param viewManager the navigation manager.
     */
    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    /**
     * Initializes the controller after the FXML is loaded.
     * Synchronises the theme toggle button's label with the current theme.
     */
    @FXML
    private void initialize() {
        ThemeManager.syncButtonLabel(themeToggleBtn);
    }

    /**
     * Navigates to the login screen.
     */
    @FXML private void goLogin() { if (viewManager != null) viewManager.toLogin(); }

    /**
     * Navigates to the registration screen.
     */
    @FXML private void goRegister() { if (viewManager != null) viewManager.toRegister(); }

    /**
     * Toggles the application theme (light/dark) and updates the button label.
     */
    @FXML
    private void onToggleTheme() {
        if (viewManager == null) return;
        viewManager.toggleTheme();
        ThemeManager.syncButtonLabel(themeToggleBtn);
    }
}