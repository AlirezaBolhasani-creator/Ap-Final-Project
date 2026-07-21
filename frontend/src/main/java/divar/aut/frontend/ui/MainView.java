package divar.aut.frontend.ui;

import divar.aut.frontend.controller.MainViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

/**
 * UI screen class for the main application view.
 * <p>
 * This is the central screen displayed after a user logs in. It loads the
 * {@code MainView.fxml} layout and initialises its controller with the provided
 * {@link ViewManager} using a custom controller factory. The resulting view
 * can be retrieved via {@link #getView()} for display in the primary stage.
 * </p>
 * <p>
 * The custom controller factory is necessary because {@link MainViewController}
 * requires a {@link ViewManager} argument in its constructor, which differs
 * from the default no‑arg constructor expected by FXMLLoader.
 * </p>
 */
public class MainView {

    private final Parent view;

    /**
     * Constructs the main view by loading the FXML and wiring up the controller
     * with the provided view manager.
     * <p>
     * The FXML file is expected to be located at {@code /MainView.fxml} in the
     * classpath (typically under {@code src/main/resources/}). If the file is
     * not found, an {@link IllegalStateException} is thrown.
     * </p>
     *
     * @param viewManager the navigation manager for screen switching and state.
     * @throws IllegalStateException if the FXML file cannot be found.
     * @throws RuntimeException if the FXML file cannot be loaded.
     */
    public MainView(ViewManager viewManager) {
        try {
            // فایل‌ها مستقیم داخل resources هستن (بدون subpackage)
            URL fxmlUrl = getClass().getResource("/MainView.fxml");
            System.out.println("=== FXML URL: " + fxmlUrl);

            if (fxmlUrl == null) {
                throw new IllegalStateException(
                        "\n❌ MainView.fxml پیدا نشد!\n" +
                                "   فایل باید اینجا باشه:\n" +
                                "   src/main/resources/MainView.fxml\n"
                );
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            loader.setControllerFactory(cls -> {
                if (cls == MainViewController.class)
                    return new MainViewController(viewManager);
                try {
                    return cls.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            view = loader.load();

        } catch (IOException e) {
            throw new RuntimeException("خطا در لود MainView.fxml", e);
        }
    }

    /**
     * Returns the loaded JavaFX root node for this screen.
     *
     * @return the {@link Parent} view to be displayed.
     */
    public Parent getView() { return view; }
}