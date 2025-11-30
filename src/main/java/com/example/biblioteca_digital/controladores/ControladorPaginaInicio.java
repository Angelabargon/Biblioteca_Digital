package com.example.biblioteca_digital.controladores;

/**
 * Hacemos los imports necesarios.
 */
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controlador de la vista de página de inicio.
 *
 * Gestiona la navegación inicial del usuario, permitiéndole
 * acceder a la pantalla de Login o a la pantalla de Registro.
 */
public class ControladorPaginaInicio {

    /** Botón que redirige a la vista de Registro. */
    @FXML private Button registro;

    /** Botón que redirige a la vista de Login. */
    @FXML private Button login;

    /** Constructor vacío requerido por JavaFX. */
    public ControladorPaginaInicio() {}

    /**
     * Cambia la vista actual a la pantalla de Login.
     *
     * @param event evento de acción generado al pulsar el botón de Login.
     */
    public void irALogin(javafx.event.ActionEvent event) {
        Navegacion.cambiarVista(event, "/com/example/biblioteca_digital/vistas/Vista-Login.fxml", "Login");
    }

    /**
     * Cambia la vista actual a la pantalla de Registro.
     *
     * @param event evento de acción generado al pulsar el botón de Registro.
     */
    public void irARegistro(javafx.event.ActionEvent event) {
        Navegacion.cambiarVista(event, "/com/example/biblioteca_digital/vistas/Vista-Registro.fxml", "Registro");
    }
}
