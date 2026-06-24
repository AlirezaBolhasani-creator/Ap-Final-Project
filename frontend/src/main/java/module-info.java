module divar.aut.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.net.http;

    opens divar.aut.frontend to javafx.fxml;
    exports divar.aut.frontend;
}