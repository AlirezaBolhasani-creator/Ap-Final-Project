package divar.aut.frontend.ui;

import javafx.scene.Parent;
import javafx.scene.control.Button;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * App‑wide light/dark theme toggle.
 * <p>
 * This app does not create a new Scene per screen — {@link ViewManager} swaps a
 * single Parent in and out of one shared StackPane/Scene (see
 * DivarApplication + ViewManager#show). Each FXML root also carries its
 * own {@code stylesheets} attribute (e.g. stylesheets="@theme.css"), so the
 * stylesheet list lives on the Parent, not the Scene.
 * </p>
 * <p>
 * ThemeManager therefore works Parent‑by‑Parent: whenever a screen is
 * shown, ViewManager calls {@link #applyCurrentMode(Parent)} so it picks
 * up whichever mode is currently active app‑wide.
 * </p>
 */
public final class ThemeManager {

    private ThemeManager() {}

    /**
     * Global flag indicating whether light mode is currently active.
     * Shared across every screen for the lifetime of the application.
     */
    private static boolean lightMode = false;

    /**
     * Mapping from base stylesheet file names to their light‑variant counterparts.
     * <p>
     * Used to determine which stylesheet to add or remove when toggling themes.
     * </p>
     */
    private static final Map<String, String> LIGHT_VARIANTS = new LinkedHashMap<>();
    static {
        LIGHT_VARIANTS.put("theme.css", "theme-light.css");
        LIGHT_VARIANTS.put("AdminDashboard.css", "AdminDashboard-light.css");
    }

    /**
     * Returns whether light mode is currently enabled.
     *
     * @return {@code true} if light mode is active, {@code false} for dark mode.
     */
    public static boolean isLightMode() {
        return lightMode;
    }

    /**
     * Toggles the global theme mode between light and dark.
     * <p>
     * After toggling, the caller is responsible for reapplying the mode
     * to the currently visible view using {@link #applyCurrentMode(Parent)}.
     * </p>
     */
    public static void toggle() {
        lightMode = !lightMode;
    }

    /**
     * Applies the current global theme mode to a given view.
     * <p>
     * This method ensures that the view's stylesheet list matches the
     * current mode. It is safe to call on every screen every time it is shown.
     * </p>
     *
     * @param view the JavaFX parent node to which the theme should be applied.
     */
    public static void applyCurrentMode(Parent view) {
        if (view == null) return;

        for (Map.Entry<String, String> entry : LIGHT_VARIANTS.entrySet()) {
            String base = entry.getKey();
            String lightFile = entry.getValue();

            String baseUrl = findStylesheetEndingWith(view, base);
            if (baseUrl == null) continue; // this screen doesn't use that base stylesheet

            String lightUrl = baseUrl.substring(0, baseUrl.length() - base.length()) + lightFile;

            boolean alreadyApplied = view.getStylesheets().contains(lightUrl);
            if (lightMode && !alreadyApplied) {
                view.getStylesheets().add(lightUrl);
            } else if (!lightMode && alreadyApplied) {
                view.getStylesheets().remove(lightUrl);
            }
        }
    }

    /**
     * Syncs a toggle button's label to the current theme mode.
     * <p>
     * For example, when in light mode, the button label will show
     * "حالت تیره" (dark mode) to indicate what will happen on click.
     * </p>
     *
     * @param button the button whose label should be updated.
     */
    public static void syncButtonLabel(Button button) {
        if (button == null) return;
        button.setText(lightMode ? "حالت تیره" : "حالت روشن");
    }

    /**
     * Applies the appropriate background style to the outer shell (StackPane)
     * that sits behind all screens.
     * <p>
     * The outer StackPane is not styled by an FXML stylesheet, so its background
     * is set directly via inline CSS.
     * </p>
     *
     * @param shellRoot the StackPane that wraps the application's content.
     */
    public static void applyShellBackground(javafx.scene.layout.StackPane shellRoot) {
        if (shellRoot == null) return;
        String style = lightMode
                ? "-fx-background-color: linear-gradient(to bottom right, #fffdf7, #fffaf0 60%, #fffaf0);"
                : "-fx-background-color: linear-gradient(to bottom right, #0c1830, #0a1120 60%, #0a1120);";
        shellRoot.setStyle(style);
    }

    /**
     * Finds a stylesheet URL in a view's stylesheet list that ends with the given suffix.
     *
     * @param view   the view whose stylesheets to search.
     * @param suffix the suffix to match (e.g., "theme.css").
     * @return the full URL of the matching stylesheet, or {@code null} if not found.
     */
    private static String findStylesheetEndingWith(Parent view, String suffix) {
        for (String url : view.getStylesheets()) {
            if (url.endsWith(suffix)) return url;
        }
        return null;
    }
}