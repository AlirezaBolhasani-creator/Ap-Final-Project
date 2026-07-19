package divar.aut.frontend.ui;
import divar.aut.frontend.net.AdService;
import divar.aut.frontend.SessionManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import divar.aut.frontend.DivarApplication;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

public class ViewManager
{
    private final StackPane root;
    private final DivarApplication mainApp;

    public ViewManager(StackPane root, DivarApplication mainApp)
    {
        this.root = root;
        this.mainApp = mainApp;
    }
    public void show(Parent newView)
    {
        if(!root.getChildren().isEmpty())
            root.getChildren().removeFirst();
        root.getChildren().add(newView);
    }
    public void toWelcome() { show(loadAuth("/Welcome.fxml")); }
    public void toLogin() { show(loadAuth("/Login.fxml")); }
    public void toRegister() { show(loadAuth("/Register.fxml")); }

    private Parent loadAuth(String resource) {
        try {
            URL url = getClass().getResource(resource);
            if (url == null) throw new IllegalStateException("FXML not found: " + resource);
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Object controller = loader.getController();
            try {
                controller.getClass().getMethod("setViewManager", ViewManager.class).invoke(controller, this);
            } catch (NoSuchMethodException ignored) {
                // controller has no setViewManager — nothing to wire
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return root;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + resource, e);
        }
    }
    public void toMain(){
        MainView mainView = new MainView(this);
        show(mainView.getView());
    }
    public void toPostAd() {
        show(new PostAdScreen(this, new AdService()).getView());
    }

    public void toFavorites() {
        show(new FavoritesScreen(this).getView());
    }

    public void toConversations() {
        show(new ConversationsScreen(this).getView());
    }

    public void toAdminDashboard() {
        AdService adService = new AdService();
        AdminDashboardScreen adminScreen = new AdminDashboardScreen(this, adService);
        root.getChildren().setAll(adminScreen.getView());
    }

    public String getUserToken() {
        return SessionManager.getInstance().getToken();
    }
    public void setUserToken(String userToken) {
        SessionManager.getInstance().setToken(userToken);
    }
    public void setUserRole(String role) { SessionManager.getInstance().setRole(role); }
    public String getUserRole() { return SessionManager.getInstance().getRole(); }

}
