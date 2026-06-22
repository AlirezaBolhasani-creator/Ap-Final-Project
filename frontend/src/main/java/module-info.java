module divar.aut.frontend {
    requires javafx.controls;
    requires javafx.fxml;

    opens divar.aut.frontend to javafx.fxml;
    exports divar.aut.frontend;
}