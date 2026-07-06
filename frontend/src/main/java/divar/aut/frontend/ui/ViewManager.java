package divar.aut.frontend.ui;
import divar.aut.frontend.net.AdService;
import divar.aut.frontend.SessionManager;
import javafx.scene.Parent;
import divar.aut.frontend.DivarApplication;
import javafx.scene.layout.StackPane;

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
    public void toWelcome() { show(new WelcomeScreen(this).getView()); }
    public void toLogin() { show(new LoginScreen(this).getView()); }
    public void toRegister() { show(new RegisterScreen(this).getView()); }
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
