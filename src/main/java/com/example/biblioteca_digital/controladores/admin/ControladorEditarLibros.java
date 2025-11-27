package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.modelos.Libro;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ControladorEditarLibros {

    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private ComboBox<String> comboCategoria;
    @FXML private TextField txtIsbn;
    @FXML private TextField txtFoto;
    @FXML private TextField txtCantidad;
    @FXML private TextArea txtDescripcion;

    private Libro libro;
    private Stage stage;
    private Runnable onGuardarCallback;

    public void setStage(Stage s) { this.stage = s; }

    public void setLibro(Libro l) {
        this.libro = l;

        // Cargar categorías (puedes cargarlas desde BD si quieres)
        comboCategoria.getItems().setAll(
                "Ficción", "Novela", "Historia", "Ciencia", "Educación",
                "Infantil", "Arte", "Fantasía", "Tecnología"
        );

        if (l != null) {
            txtTitulo.setText(l.getTitulo());
            txtAutor.setText(l.getAutor());
            comboCategoria.setValue(l.getGenero());
            txtIsbn.setText(l.getIsbn());
            txtFoto.setText(l.getFoto());
            txtCantidad.setText(String.valueOf(l.getCantidad()));
            txtDescripcion.setText(l.getDescripcion());
        }
    }

    public Libro getLibroResultado() {
        if (libro == null) libro = new Libro();

        libro.setTitulo(txtTitulo.getText());
        libro.setAutor(txtAutor.getText());
        libro.setGenero(comboCategoria.getValue());
        libro.setIsbn(txtIsbn.getText());
        libro.setFoto(txtFoto.getText());
        libro.setDescripcion(txtDescripcion.getText());

        try { libro.setCantidad(Integer.parseInt(txtCantidad.getText())); }
        catch (Exception e) { libro.setCantidad(1); }

        libro.setDisponible(libro.getCantidad() > 0);
        return libro;
    }

    @FXML
    private void guardar() {
        if (onGuardarCallback != null) onGuardarCallback.run();
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

    public void setOnGuardarCallback(Runnable cb) { this.onGuardarCallback = cb; }
}
