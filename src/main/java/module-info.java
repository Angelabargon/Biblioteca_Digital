module com.example.biblioteca_digital {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires javafx.graphics;

    opens com.example.biblioteca_digital to javafx.fxml;
    exports com.example.biblioteca_digital;
    exports com.example.biblioteca_digital.controladores;
    opens com.example.biblioteca_digital.controladores to javafx.fxml;
    exports com.example.biblioteca_digital.modelos;
    opens com.example.biblioteca_digital.modelos to javafx.fxml;
}