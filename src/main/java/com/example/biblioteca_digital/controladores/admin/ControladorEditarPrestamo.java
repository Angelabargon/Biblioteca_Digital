package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.modelos.Estado;
import com.example.biblioteca_digital.modelos.Prestamo;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class ControladorEditarPrestamo {

    @FXML private TextField comboUsuario;
    @FXML private TextField comboLibro;
    @FXML private DatePicker fechaInicio;
    @FXML private DatePicker fechaFin;
    @FXML private ComboBox<Estado> comboEstado;

    private Prestamo prestamo;
    private Stage stage;
    private Runnable onGuardarCallback;

    @FXML private void initialize() { comboEstado.getItems().setAll(Estado.values()); }

    public void setStage(Stage s) { this.stage = s; }
    public void setPrestamo(Prestamo p) {
        this.prestamo = p;
        if (p!=null) {
            comboUsuario.setText(String.valueOf(p.getId_usuario()));
            comboLibro.setText(String.valueOf(p.getId_libro()));
            fechaInicio.setValue(p.getFecha_inicio()!=null? p.getFecha_inicio() : LocalDate.now());
            fechaFin.setValue(p.getFecha_fin()!=null? p.getFecha_fin() : LocalDate.now().plusWeeks(2));
            comboEstado.setValue(Estado.valueOf(p.getEstado()));
        }
    }
    public Prestamo getPrestamoResultado() {
        if (prestamo==null) prestamo = new Prestamo();
        try { prestamo.setId_usuario(Integer.parseInt(comboUsuario.getText())); } catch (Exception ignored) {}
        try { prestamo.setId_libro(Integer.parseInt(comboLibro.getText())); } catch (Exception ignored) {}
        prestamo.setFecha_inicio(fechaInicio.getValue());
        prestamo.setFecha_fin(fechaFin.getValue());
        prestamo.setEstado(String.valueOf(comboEstado.getValue()));
        return prestamo;
    }
    @FXML private void guardar() { if (onGuardarCallback!=null) onGuardarCallback.run(); if (stage!=null) stage.close(); }
    @FXML private void cancelar() { if (stage!=null) stage.close(); }
    public void setOnGuardarCallback(Runnable cb) { this.onGuardarCallback = cb; }
}
