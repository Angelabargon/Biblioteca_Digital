package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.DAO.admin.LibroAdminDAO;
import com.example.biblioteca_digital.modelos.Libro;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControladorEditarLibros {

    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private TextField txtCategoria;  // <--- AHORA ES TEXTFIELD
    @FXML private TextField txtIsbn;
    @FXML private TextField txtFoto;
    @FXML private TextField txtCantidad;
    @FXML private TextArea txtDescripcion;
    @FXML private TextArea txtContenido;

    private Libro libro;
    private Stage stage;
    private Runnable onGuardarCallback;
    private final LibroAdminDAO dao = new LibroAdminDAO();

    @FXML
    public void initialize() {
    }

    public void setStage(Stage s) {
        this.stage = s;
    }

    public void setLibro(Libro l) {
        this.libro = l;

        if (l != null) {
            txtTitulo.setText(l.getTitulo());
            txtAutor.setText(l.getAutor());
            txtCategoria.setText(l.getGenero());
            txtIsbn.setText(l.getIsbn());
            txtFoto.setText(l.getFoto());
            txtCantidad.setText(String.valueOf(l.getCantidad()));
            txtDescripcion.setText(l.getDescripcion());
            txtContenido.setText(l.getContenido());
        }
    }

    public Libro getLibroResultado() {
        if (libro == null) {
            libro = new Libro();
        }

        libro.setTitulo(txtTitulo.getText());
        libro.setAutor(txtAutor.getText());
        libro.setGenero(txtCategoria.getText());
        libro.setIsbn(txtIsbn.getText());
        libro.setFoto(txtFoto.getText());
        libro.setDescripcion(txtDescripcion.getText());
        libro.setContenido(txtContenido.getText());

        try {
            libro.setCantidad(Integer.parseInt(txtCantidad.getText()));
        } catch (Exception e) {
            libro.setCantidad(1);
        }

        libro.setCantidadDisponible(libro.getCantidad());
        libro.setDisponible(libro.getCantidad() > 0);
        return libro;
    }

    @FXML
    private void guardar() {
        if (onGuardarCallback != null) {
            onGuardarCallback.run();
        }
        if (stage != null) stage.close();
    }

    @FXML
    private void cancelar() {
        if (stage != null) stage.close();
    }

    @FXML
    private void cerrarVentana() {
        if (stage != null) stage.close();
    }

    public void setOnGuardarCallback(Runnable cb) {
        this.onGuardarCallback = cb;
    }
}
