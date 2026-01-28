package com.example.biblioteca_digital.controladores.usuario;

/**
 * Imports necesarios para el test.
 */
import com.example.biblioteca_digital.controladores.ControladorLogin;
import com.example.biblioteca_digital.DAO.LoginDAO;
import com.example.biblioteca_digital.modelos.Usuario;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Clase de test unitarios para ControladorLogin.
 */
@DisplayName("Test de ControladorLogin")
class ControladorLoginTest {

    private ControladorLogin controlador;
    private LoginDAO loginDAOMock;

    /**
     * Antes de todos los test se inicia JavaFX.
     */
    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});

        } catch (IllegalStateException e) {}
    }

    /**
     * Se prepara el controlador y mok del DAO antes de cada test.
     */
    @BeforeEach
    void setUp() {
        controlador = new ControladorLogin();

        // Simulación de los elementos JavaFX.
        controlador.tf_email = new TextField();
        controlador.pf_contraseña = new PasswordField();
        controlador.tbt_usuario = new ToggleButton("usuario");
        controlador.tbt_admin = new ToggleButton("admin");

        // Se escoge un rol por defecto como en el controldor.
        controlador.tbt_usuario.setSelected(true);

        // Mock del DAO.
        loginDAOMock = mock(LoginDAO.class);

        // Reemplazo del DAO real por el mock.
        controlador.loginDAO = loginDAOMock;

    }

    @DisplayName("Comprobación de campos vacíos sin llamada a DAO")
    @Test
    void testCamposVacios_NoLlamaDAO() {
        controlador.tf_email.setText("");
        controlador.pf_contraseña.setText("");

        controlador.iniciarSesion(new ActionEvent());

        verify(loginDAOMock, never()).autenticar(anyString(), anyString(), anyString());

    }

    @DisplayName("Comprobación de llamada a DAO en caso de campos rellenados válidos")
    @Test
    void testLoginCorrecto() {
        controlador.tf_email.setText("test@mail.com");
        controlador.pf_contraseña.setText("1234");

        Usuario u = new Usuario();
        when(loginDAOMock.autenticar("test@mail.com", "1234", "usuario"))
                .thenReturn(Optional.of(u));

        controlador.iniciarSesion(new ActionEvent());

        verify(loginDAOMock, times(1))
                .autenticar("test@mail.com", "1234", "usuario");

    }

    @DisplayName("Comprobación de llamada a DAO con campos incorrectos")
    @Test
    void testLoginIncorrecto() {
        controlador.tf_email.setText("test@mail.com");
        controlador.pf_contraseña.setText("1234");

        when(loginDAOMock.autenticar(anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        controlador.iniciarSesion(new ActionEvent());

        verify(loginDAOMock, times(1))
                .autenticar("test@mail.com", "1234", "usuario");
    }
}

