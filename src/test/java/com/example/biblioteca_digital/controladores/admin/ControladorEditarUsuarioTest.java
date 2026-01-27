package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.controladores.admin.ControladorEditarUsuario;
import com.example.biblioteca_digital.modelos.Rol;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class ControladorEditarUsuarioTest {

    private ControladorEditarUsuario controlador;

    @BeforeEach
    void setUp() {
        controlador = new ControladorEditarUsuario();

        controlador.txtUsuario = new TextField();
        controlador.txtNombre = new TextField();
        controlador.txtPrimerApellido = new TextField();
        controlador.txtCorreo = new TextField();
        controlador.txtContrasena = new PasswordField();
        controlador.comboRol = new ComboBox<>();

        controlador.initialize();
        controlador.setStage(new Stage());
    }

    @Test
    void initialize_cargaRolesCorrectamente() {

        assertEquals(Rol.values().length, controlador.comboRol.getItems().size());
    }

    @Test
    void setUsuario_cargaDatosEnFormulario() {

        Usuario u = new Usuario();
        u.setNombreUsuario("admin");
        u.setNombre("Administrador");
        u.setPrimerApellido("Sistema");
        u.setCorreo("admin@biblioteca.com");
        u.setContrasena("1234");
        u.setRol(Rol.admin);

        controlador.setUsuario(u);

        assertEquals("admin", controlador.txtUsuario.getText());
        assertEquals("Administrador", controlador.txtNombre.getText());
        assertEquals("Sistema", controlador.txtPrimerApellido.getText());
        assertEquals("admin@biblioteca.com", controlador.txtCorreo.getText());
        assertEquals("1234", controlador.txtContrasena.getText());
        assertEquals(Rol.admin, controlador.comboRol.getValue());
    }

    @Test
    void getUsuarioResultado_creaUsuarioNuevo() {

        controlador.txtUsuario.setText("juan");
        controlador.txtNombre.setText("Juan");
        controlador.txtPrimerApellido.setText("Pérez");
        controlador.txtCorreo.setText("juan@email.com");
        controlador.txtContrasena.setText("pass");
        controlador.comboRol.setValue(Rol.usuario);

        Usuario u = controlador.getUsuarioResultado();

        assertNotNull(u);
        assertEquals("juan", u.getNombreUsuario());
        assertEquals("Juan", u.getNombre());
        assertEquals("Pérez", u.getPrimerApellido());
        assertEquals("juan@email.com", u.getCorreo());
        assertEquals("pass", u.getContrasena());
        assertEquals(Rol.usuario, u.getRol());
    }

    @Test
    void getUsuarioResultado_actualizaUsuarioExistente() {

        Usuario u = new Usuario();
        u.setNombreUsuario("viejo");

        controlador.setUsuario(u);

        controlador.txtUsuario.setText("nuevo");
        controlador.comboRol.setValue(Rol.admin);

        Usuario resultado = controlador.getUsuarioResultado();

        assertSame(u, resultado);
        assertEquals("nuevo", resultado.getNombreUsuario());
        assertEquals(Rol.admin, resultado.getRol());
    }

    @Test
    void guardar_ejecutaCallback() {

        AtomicBoolean ejecutado = new AtomicBoolean(false);
        controlador.setOnGuardarCallback(() -> ejecutado.set(true));

        controlador.guardar();

        assertTrue(ejecutado.get());
    }
}
