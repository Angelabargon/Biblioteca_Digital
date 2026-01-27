package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.controladores.ControladorRegistro;
import com.example.biblioteca_digital.modelos.Rol;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Tests unitarios para la clase REGISTRO
 */
@DisplayName("Tests del registro")
class ControladorRegistroTest
{
    private ControladorRegistro controlador;
    /**
     * Se ejecuta al inicio para que JavaFX vaya
     */
    @BeforeAll
    static void initJavaFX()
    {
        try
        {
            Platform.startup(() -> {});
        }
        catch (IllegalStateException e)
        { }
    }
    /**
     * Se ejecuta antes de cada test
     */
    @BeforeEach
    void setUp()
    {
        controlador = new ControladorRegistro();
        controlador.aceptoTerminos = new CheckBox();
    }

    /**
     * Se ejecuta después de cada test
     */
    @AfterEach
    void tearDown()
    {
        controlador.aceptoTerminos = null;
        controlador = null;
    }

    @DisplayName("Comparación de dos contraseñas iguales")
    @Test
    void testCompararContrasenasIguales()
    {
        // AssertTrue (Verificar que sea verdadero)
        assertTrue(controlador.compararContrasenas("12345678", "12345678"));
    }

    @DisplayName("Comparación de dos contraseñas distintas")
    @Test
    void testCompararContrasenasDistintas()
    {
        // AssertFalse (Verificar que sea falso)
        assertFalse(controlador.compararContrasenas("12345678", "34243243"));
    }

    @DisplayName("Validación 1 de tres campos con algo escrito")
    @Test
    void testValidarCamposNoVaciosCorrecto()
    {
        // AssertTrue (Verificar que sea verdadero)
        assertTrue(controlador.validarCamposNoVacios("a", "a", "a"));
    }

    @DisplayName("Validación 2 de tres campos con algo o nada escrito")
    @Test
    void testValidarCamposConCampoVacio1()
    {
        // AssertFalse (Verificar que sea falso)
        assertFalse(controlador.validarCamposNoVacios("a", "", ""));
    }

    @DisplayName("Validación 3 de tres campos con algo o nada escrito")
    @Test
    void testValidarCamposConCampoVacio2()
    {
        // AssertFalse (Verificar que sea falso)
        assertFalse(controlador.validarCamposNoVacios("", "a", ""));
    }

    @DisplayName("Validación 4 de tres campos con algo o nada escrito")
    @Test
    void testValidarCamposConCampoVacio3()
    {
        // AssertFalse (Verificar que sea falso)
        assertFalse(controlador.validarCamposNoVacios("", "", "a"));
    }

    @DisplayName("Validación 5 de tres campos con algo o nada escrito")
    @Test
    void testValidarCamposConCampoVacio4()
    {
        // AssertFalse (Verificar que sea falso)
        assertFalse(controlador.validarCamposNoVacios("a", "a", ""));
    }

    @DisplayName("Validación 6 de tres campos con algo o nada escrito")
    @Test
    void testValidarCamposConCampoVacio5()
    {
        // AssertFalse (Verificar que sea falso)
        assertFalse(controlador.validarCamposNoVacios("a", "", "a"));
    }

    @DisplayName("Validación 7 de tres campos con algo o nada escrito")
    @Test
    void testValidarCamposConCampoVacio6()
    {
        // AssertFalse (Verificar que sea falso)
        assertFalse(controlador.validarCamposNoVacios("", "a", "a"));
    }

    @DisplayName("Validación 1 de que de tres campos, uno de los campos es nulo")
    @Test
    void testValidarCamposConNull1()
    {
        // AssertFalse (Verificar que sea falso)
        assertFalse(controlador.validarCamposNoVacios("a", null, "a"));
    }

    @DisplayName("Validación 2 de que de tres campos, uno de los campos es nulo")
    @Test
    void testValidarCamposConNull2()
    {
        // AssertFalse (Verificar que sea falso)
        assertFalse(controlador.validarCamposNoVacios("a", "a", null));
    }

    @DisplayName("Validación 3 de que de tres campos, uno de los campos es nulo")
    @Test
    void testValidarCamposConNull3()
    {
        // AssertFalse (Verificar que sea falso)
        assertFalse(controlador.validarCamposNoVacios(null, "a", "a"));
    }

    @DisplayName("Validación de que el checkbox no está marcado")
    @Test
    void testCheckboxNoMarcado()
    {
        controlador.aceptoTerminos.setSelected(false);
        // AssertFalse (Verificar que sea falso)
        assertFalse(controlador.verificarCheckboxTickeado());
    }

    @DisplayName("Validación de que el checkbox si está marcado")
    @Test
    void testCheckboxMarcado()
    {
        controlador.aceptoTerminos.setSelected(true);
        // AssertTrue (Verificar que sea verdadero)
        assertTrue(controlador.verificarCheckboxTickeado());
    }

    @DisplayName("Validación de que el usuario se crea bien")
    @Test
    void testConstruirObjetoUsuario()
    {
        //Se crea un usuario nuevo
        LocalDate fecha = LocalDate.now();
        Usuario usuario = controlador.construirObjetoUsuario(
                1,
                "Angela01",
                "Ángela",
                "Bárcena",
                "angelabarcena@gmail.com",
                "12345678",
                Rol.usuario,
                fecha
        );
        //AssertNotNull (Verificar que el usuario exista y no sea nulo).
        assertNotNull(usuario);
        //AssertEquals (Verificar que el ID sea el mismo que el que se guarda).
        assertEquals(1, usuario.getId());
        //AssertEquals (Verificar que el nombre del usuario sea el mismo que el que se guarda).
        assertEquals("Angela01", usuario.getNombre());
        //AssertEquals (Verificar que el nombre privado sea el mismo que el que se guarda). (lo puse al revés inicialmente)
        assertEquals("Ángela", usuario.getNombreUsuario());
        // AssertEquals (Verificar que el rol del usuario sea el mismo que el que se guarda).
        assertEquals(Rol.usuario, usuario.getRol());
        // AssertEquals (Verificar que la fecha en la que se registró el usuario sea la misma que la que se guarda).
        assertEquals(fecha, usuario.getFechaRegistro());
    }
}

