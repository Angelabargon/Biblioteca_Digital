package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.modelos.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class ControladorEditarPrestamoTest {

    private ControladorEditarPrestamo controlador;

    @BeforeEach
    void setUp() {
        controlador = new ControladorEditarPrestamo();

        controlador.lblTitulo = new Label();
        controlador.btnGuardar = new Button();
        controlador.comboUsuario = new ComboBox<>();
        controlador.comboLibro = new ComboBox<>();
        controlador.comboEstado = new ComboBox<>();
        controlador.fechaFin = new DatePicker();

        controlador.comboEstado.getItems().addAll("activo", "bloqueado");

        controlador.setStage(new Stage());
    }

    @Test
    void prepararNuevoPrestamo_inicializaCorrectamente() {

        controlador.prepararNuevoPrestamo();

        Prestamo p = controlador.getPrestamoResultado();

        assertNotNull(p);
        assertEquals("Nuevo Préstamo", controlador.lblTitulo.getText());
        assertEquals("Crear Préstamo", controlador.btnGuardar.getText());
    }

    @Test
    void setPrestamoEditar_cargaDatosCorrectamente() {

        Usuario u = new Usuario();
        u.setNombreUsuario("juan");

        Libro l = new Libro();
        l.setTitulo("1984");

        Prestamo p = new Prestamo();
        p.setUsuario(u);
        p.setLibro(l);
        p.setFecha_fin(LocalDate.now());
        p.setEstado("activo");

        controlador.setPrestamoEditar(p);

        assertEquals("Editar Préstamo", controlador.lblTitulo.getText());
        assertEquals("Guardar Cambios", controlador.btnGuardar.getText());
        assertEquals(u, controlador.comboUsuario.getValue());
        assertEquals(l, controlador.comboLibro.getValue());
        assertEquals("activo", controlador.comboEstado.getValue());
        assertTrue(controlador.comboUsuario.isDisable());
        assertTrue(controlador.comboLibro.isDisable());
    }

    @Test
    void guardar_actualizaPrestamoYEjecutaCallback() {

        Prestamo p = new Prestamo();
        controlador.setPrestamoEditar(p);

        controlador.fechaFin.setValue(LocalDate.of(2026, 1, 1));
        controlador.comboEstado.setValue("activo");

        AtomicBoolean callbackEjecutado = new AtomicBoolean(false);
        controlador.setOnGuardarCallback(() -> callbackEjecutado.set(true));

        controlador.guardar();

        assertEquals(LocalDate.of(2026, 1, 1), p.getFecha_fin());
        assertEquals("activo", p.getEstado());
        assertTrue(callbackEjecutado.get());
    }

    @Test
    void guardar_sinCamposNoActualizaPrestamo() {

        Prestamo p = new Prestamo();
        controlador.setPrestamoEditar(p);

        controlador.guardar();

        assertNull(p.getFecha_fin());
        assertNull(p.getEstado());
    }

}

