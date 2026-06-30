module divar.aut.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.net.http;
    requires com.google.gson;

    opens divar.aut.frontend to javafx.fxml;
    exports divar.aut.frontend;
    exports divar.aut.frontend.ui;
    opens divar.aut.frontend.ui to javafx.fxml;
    exports divar.aut.frontend.authpart;
    opens divar.aut.frontend.authpart to javafx.fxml;
}