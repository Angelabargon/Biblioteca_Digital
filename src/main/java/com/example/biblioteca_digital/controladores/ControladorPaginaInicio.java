package com.example.biblioteca_digital.controladores;

/**
 * Hacemos los imports necesarios.
 */
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controlador de la página de inicio que se encarga de la navegación a Login o Registro.
 */
public class ControladorPaginaInicio {

    /**
     * Definimos los elementos que recibiran los métodos en la vista:
     *
     * - Button registro: Llevará al usuario a Vista-Registro.
     * - Button login: Llevará al usuario a Vista-Login.
     */
    @FXML
    private Button registro;
    @FXML
    private Button login;

    public ControladorPaginaInicio() {}

    /**
     * Cambia a la vista de Login.
     *
     * @param event
     */
    public void irALogin(javafx.event.ActionEvent event) {
        Navegacion.cambiarVista(event, "/com/example/biblioteca_digital/vistas/Vista-Login.fxml", "Login");

    }
    /**
     * Cambia a la vista de Registro.
     *
     * @param event
     */
    public void irARegistro(javafx.event.ActionEvent event) {
        Navegacion.cambiarVista(event, "/com/example/biblioteca_digital/vistas/Vista-Registro.fxml", "Registro");
    }
}
