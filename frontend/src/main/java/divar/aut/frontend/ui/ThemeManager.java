package divar.aut.frontend.ui;

import javafx.scene.Parent;
import javafx.scene.control.Button;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * App-wide light/dark theme toggle.
 *
 * This app doesn't create a new Scene per screen — ViewManager swaps a
 * single Parent in and out of one shared StackPane/Scene (see
 * DivarApplication + ViewManager#show). Each FXML root also carries its
 * own "stylesheets" attribute (e.g. stylesheets="@theme.css"), so the
 * stylesheet list lives on the Parent, not the Scene.
 *
 * ThemeManager therefore works Parent-by-Parent: whenever a screen is
 * shown, ViewManager calls ThemeManager.applyCurrentMode(view) so it picks
 * up whichever mode is currently active app-wide.
 */
public final class ThemeManager {

    private ThemeManager() {}

    /** Global flag — shared across every screen for the lifetime of the app. */
    private static boolean lightMode = false;

    // base stylesheet file name -> its light counterpart's file name
    private static final Map<String, String> LIGHT_VARIANTS = new LinkedHashMap<>();
    static {
        LIGHT_VARIANTS.put("theme.css", "theme-light.css");
        LIGHT_VARIANTS.put("AdminDashboard.css", "AdminDashboard-light.css");
    }

    public static boolean isLightMode() {
        return lightMode;
    }

    /** Flip the global mode. Caller is responsible for re-applying it to the visible view. */
    public static void toggle() {
        lightMode = !lightMode;
    }

    /**
     * Make sure the given root's stylesheet list matches the current global mode.
     * Safe to call on every screen, every time it's shown.
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

    /** Convenience: sync a toggle button's label with the current mode. */
    public static void syncButtonLabel(Button button) {
        if (button == null) return;
        button.setText(lightMode ? "حالت تیره" : "حالت روشن");
    }

    /** The outer StackPane (behind every screen) isn't styled by an FXML stylesheet — set its background directly. */
    public static void applyShellBackground(javafx.scene.layout.StackPane shellRoot) {
        if (shellRoot == null) return;
        String style = lightMode
                ? "-fx-background-color: linear-gradient(to bottom right, #fffdf7, #fffaf0 60%, #fffaf0);"
                : "-fx-background-color: linear-gradient(to bottom right, #0c1830, #0a1120 60%, #0a1120);";
        shellRoot.setStyle(style);
    }

    private static String findStylesheetEndingWith(Parent view, String suffix) {
        for (String url : view.getStylesheets()) {
            if (url.endsWith(suffix)) return url;
        }
        return null;
    }
}
