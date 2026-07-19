package divar.aut.frontend.controller;

import divar.aut.frontend.ui.ViewManager;
import javafx.fxml.FXML;

public class WelcomeController {
    private ViewManager viewManager;

    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    @FXML private void goLogin() { if (viewManager != null) viewManager.toLogin(); }
    @FXML private void goRegister() { if (viewManager != null) viewManager.toRegister(); }
}
