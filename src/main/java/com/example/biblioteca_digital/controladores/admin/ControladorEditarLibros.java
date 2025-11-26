package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.modelos.Libro;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ControladorEditarLibros {

    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private TextField txtGenero;
    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtCantidad;

    private Libro libro;
    private Stage stage;
    private Runnable onGuardarCallback;

    public void setStage(Stage s) { this.stage = s; }
    public void setLibro(Libro l) {
        this.libro = l;
        if (l!=null) {
            txtTitulo.setText(l.getTitulo());
            txtAutor.setText(l.getAutor());
            txtGenero.setText(l.getGenero());
            txtDescripcion.setText(l.getDescripcion());
            txtCantidad.setText(String.valueOf(l.getCantidad()));
        }
    }
    public Libro getLibroResultado() {
        if (libro==null) libro = new Libro();
        libro.setTitulo(txtTitulo.getText());
        libro.setAutor(txtAutor.getText());
        libro.setGenero(txtGenero.getText());
        libro.setDescripcion(txtDescripcion.getText());
        try { libro.setCantidad(Integer.parseInt(txtCantidad.getText())); } catch (Exception e) { libro.setCantidad(1); }
        libro.setDisponible(libro.getCantidad()>0);
        return libro;
    }
    @FXML private void guardar() { if (onGuardarCallback!=null) onGuardarCallback.run(); if (stage!=null) stage.close(); }
    @FXML private void cancelar() { if (stage!=null) stage.close(); }
    public void setOnGuardarCallback(Runnable cb) { this.onGuardarCallback = cb; }
}
