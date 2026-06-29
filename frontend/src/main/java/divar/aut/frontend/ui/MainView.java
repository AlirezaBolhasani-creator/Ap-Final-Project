package divar.aut.frontend.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

public class MainView {

    private final Parent view;

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

    public Parent getView() { return view; }
}
 