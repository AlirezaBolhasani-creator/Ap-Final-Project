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
    exports divar.aut.frontend.model;
    opens divar.aut.frontend.model to javafx.fxml, com.google.gson;
    exports divar.aut.frontend.net;
    opens divar.aut.frontend.net to javafx.fxml, com.google.gson;
    exports divar.aut.frontend.config;
    opens divar.aut.frontend.config to javafx.fxml;
    exports divar.aut.frontend.controller;
    opens divar.aut.frontend.controller to javafx.fxml;

}