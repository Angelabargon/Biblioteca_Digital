module com.example.biblioteca_digital {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.base;
    requires com.example.biblioteca_digital;

    opens com.example.biblioteca_digital to javafx.fxml;
    opens com.example.biblioteca_digital.controladores to javafx.fxml;
    opens com.example.biblioteca_digital.modelos to javafx.fxml;
    opens com.example.biblioteca_digital.controladores.usuario to javafx.fxml;

    exports com.example.biblioteca_digital;
    exports com.example.biblioteca_digital.controladores;
    exports com.example.biblioteca_digital.modelos;
    exports com.example.biblioteca_digital.controladores.usuario;


}