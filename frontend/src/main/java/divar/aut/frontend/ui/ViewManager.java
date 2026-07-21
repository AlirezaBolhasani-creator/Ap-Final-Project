package divar.aut.frontend.ui;

import divar.aut.frontend.net.AdService;
import divar.aut.frontend.SessionManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import divar.aut.frontend.DivarApplication;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

/**
 * Central navigation manager for the application.
 * <p>
 * Manages screen switching by swapping the current view inside a shared
 * {@link StackPane} (the root container). Also handles theme toggling,
 * session token/role management, and creation of various screens via
 * their respective UI classes.
 * </p>
 * <p>
 * Navigation methods delegate to {@link #show(Parent)}, which applies the
 * current theme to the new view before displaying it.
 * </p>
 */
public class ViewManager {

    private final StackPane root;
    private final DivarApplication mainApp;

    /**
     * Constructs a ViewManager with the application's root container and main application reference.
     *
     * @param root    the shared {@link StackPane} that holds the current view.
     * @param mainApp the main application instance (unused internally but kept for future use).
     */
    public ViewManager(StackPane root, DivarApplication mainApp) {
        this.root = root;
        this.mainApp = mainApp;
    }

    /**
     * Displays a new view in the root container.
     * <p>
     * Applies the current theme to the new view, removes any existing child,
     * and adds the new view as the root's only child.
     * </p>
     *
     * @param newView the JavaFX parent node to display.
     */
    public void show(Parent newView) {
        ThemeManager.applyCurrentMode(newView);
        if (!root.getChildren().isEmpty())
            root.getChildren().removeFirst();
        root.getChildren().add(newView);
    }

    /**
     * Toggles the application theme between light and dark modes.
     * <p>
     * Updates the global theme flag, reapplies the background to the root
     * shell, and updates the currently visible view to reflect the new mode.
     * </p>
     */
    public void toggleTheme() {
        ThemeManager.toggle();
        ThemeManager.applyShellBackground(root);
        if (!root.getChildren().isEmpty()) {
            ThemeManager.applyCurrentMode((Parent) root.getChildren().get(0));
        }
    }

    /**
     * Navigates to the welcome screen.
     */
    public void toWelcome() { show(loadAuth("/Welcome.fxml")); }

    /**
     * Navigates to the login screen.
     */
    public void toLogin() { show(loadAuth("/Login.fxml")); }

    /**
     * Navigates to the registration screen.
     */
    public void toRegister() { show(loadAuth("/Register.fxml")); }

    /**
     * Loads an authentication-related FXML screen and injects this ViewManager
     * into its controller (if the controller has a {@code setViewManager} method).
     *
     * @param resource the FXML file path (e.g., "/Welcome.fxml").
     * @return the loaded JavaFX root node.
     * @throws RuntimeException if the FXML file cannot be found or loaded.
     */
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

    /**
     * Navigates to the main application screen (after login).
     */
    public void toMain() {
        MainView mainView = new MainView(this);
        show(mainView.getView());
    }

    /**
     * Navigates to the "Post Ad" screen for creating a new advertisement.
     */
    public void toPostAd() {
        show(new PostAdScreen(this, new AdService()).getView());
    }

    /**
     * Navigates to the "Favorites" screen showing the user's saved ads.
     */
    public void toFavorites() {
        show(new FavoritesScreen(this).getView());
    }

    /**
     * Navigates to the "Conversations" screen listing the user's chats.
     */
    public void toConversations() {
        show(new ConversationsScreen(this).getView());
    }
    /**
     * Navigates to the admin dashboard (admin only).
     */
    public void toAdminDashboard() {
        AdService adService = new AdService();
        AdminDashboardScreen adminScreen = new AdminDashboardScreen(this, adService);
        show(adminScreen.getView());
    }

    /**
     * Returns the current user's authentication token.
     *
     * @return the token string, or {@code null} if not authenticated.
     */
    public String getUserToken() {
        return SessionManager.getInstance().getToken();
    }

    /**
     * Sets the current user's authentication token.
     *
     * @param userToken the token to store.
     */
    public void setUserToken(String userToken) {
        SessionManager.getInstance().setToken(userToken);
    }

    /**
     * Sets the current user's role (e.g., "USER" or "ADMIN").
     *
     * @param role the role to store.
     */
    public void setUserRole(String role) {
        SessionManager.getInstance().setRole(role);
    }

    /**
     * Returns the current user's role.
     *
     * @return the role string, or {@code null} if not set.
     */
    public String getUserRole() {
        return SessionManager.getInstance().getRole();
    }
}