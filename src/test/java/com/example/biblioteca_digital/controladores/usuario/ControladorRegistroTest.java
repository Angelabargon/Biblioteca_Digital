package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.controladores.ControladorRegistro;
import com.example.biblioteca_digital.modelos.Rol;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.scene.control.CheckBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ControladorRegistroTest
{
    private ControladorRegistro controlador;
    @BeforeEach
    void setUp()
    {
        controlador = new ControladorRegistro();
        controlador.aceptoTerminos = new CheckBox();
    }

    @Test
    void testCompararContrasenasIguales() {
        assertTrue(controlador.compararContrasenas("12345678", "12345678"));
    }

    @Test
    void testCompararContrasenasDistintas() {
        assertFalse(controlador.compararContrasenas("12345678", "87654321"));
    }

    @Test
    void testValidarCamposNoVaciosCorrecto() {
        assertTrue(controlador.validarCamposNoVacios("a", "b", "c"));
    }

    @Test
    void testValidarCamposConCampoVacio() {
        assertFalse(controlador.validarCamposNoVacios("a", "", "c"));
    }

    @Test
    void testValidarCamposConNull() {
        assertFalse(controlador.validarCamposNoVacios("a", null, "c"));
    }

    @Test
    void testCheckboxNoMarcado() {
        controlador.aceptoTerminos.setSelected(false);
        assertFalse(controlador.verificarCheckboxTickeado());
    }

    @Test
    void testCheckboxMarcado() {
        controlador.aceptoTerminos.setSelected(true);
        assertTrue(controlador.verificarCheckboxTickeado());
    }

    @Test
    void testConstruirObjetoUsuario() {
        LocalDate fecha = LocalDate.now();

        Usuario usuario = controlador.construirObjetoUsuario(
                1,
                "angela",
                "Ángela",
                "Bárcena",
                "correo@test.com",
                "12345678",
                Rol.usuario,
                fecha
        );

        assertNotNull(usuario);
        assertEquals(1, usuario.getId());
        assertEquals("angela", usuario.getNombreUsuario());
        assertEquals("Ángela", usuario.getNombre());
        assertEquals(Rol.usuario, usuario.getRol());
        assertEquals(fecha, usuario.getFechaRegistro());
    }
}

