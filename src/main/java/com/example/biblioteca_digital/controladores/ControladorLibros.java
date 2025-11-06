package com.example.biblioteca_digital.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ControladorLibros {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
