module com.example.biblioteca_digital {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.biblioteca_digital to javafx.fxml;
    exports com.example.biblioteca_digital;
}