package com.example.biblioteca_digital.controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class ControladorPaginaInicio
{

    @FXML
    private Button registro;

    @FXML
    private Button login;

    public ControladorPaginaInicio()
    {}

    /**
     * Manejo de botón de inicio de sesión
     * @param event
     */
    public void irALogin(javafx.event.ActionEvent event) {
        Navegacion.cambiarVista(event, "/com/example/biblioteca_digital/vistas/Vista-Registro.fxml", "Registro");

    }
    /**
     * Manejo del botón de registro de un nuevo usuario.
     * @param event
     */
    public void irARegistro(javafx.event.ActionEvent event) {
        Navegacion.cambiarVista(event, "/com/example/biblioteca_digital/vistas/Vista-Login.fxml", "Login");
    }
}
