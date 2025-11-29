package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.DAO.admin.LibroAdminDAO;
import com.example.biblioteca_digital.modelos.Libro;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControladorEditarLibros {
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtAutor;
    @FXML
    private ComboBox<String> comboCategoria;
    @FXML
    private TextField txtIsbn;
    @FXML
    private TextField txtFoto;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private TextArea txtContenido;
    private Libro libro;
    private Stage stage;
    private Runnable onGuardarCallback;
    private final LibroAdminDAO dao = new LibroAdminDAO();

    @FXML
    public void initialize() {
        this.comboCategoria.setEditable(true);
        this.comboCategoria.setItems(FXCollections.observableArrayList(this.dao.obtenerGeneros()));
    }

    public void setStage(Stage s) {
        this.stage = s;
    }

    public void setLibro(Libro l) {
        this.libro = l;
        if (l != null) {
            this.txtTitulo.setText(l.getTitulo());
            this.txtAutor.setText(l.getAutor());
            this.comboCategoria.setValue(l.getGenero());
            this.txtIsbn.setText(l.getIsbn());
            this.txtFoto.setText(l.getFoto());
            this.txtCantidad.setText(String.valueOf(l.getCantidad()));
            this.txtDescripcion.setText(l.getDescripcion());
            this.txtContenido.setText(l.getContenido());
        }

    }

    public Libro getLibroResultado() {
        if (this.libro == null) {
            this.libro = new Libro();
        }

        this.libro.setTitulo(this.txtTitulo.getText());
        this.libro.setAutor(this.txtAutor.getText());
        this.libro.setGenero((String)this.comboCategoria.getValue());
        this.libro.setIsbn(this.txtIsbn.getText());
        this.libro.setFoto(this.txtFoto.getText());
        this.libro.setDescripcion(this.txtDescripcion.getText());
        this.libro.setContenido(this.txtContenido.getText());

        try {
            this.libro.setCantidad(Integer.parseInt(this.txtCantidad.getText()));
        } catch (Exception var2) {
            this.libro.setCantidad(1);
        }

        this.libro.setCantidadDisponible(this.libro.getCantidad());
        this.libro.setDisponible(this.libro.getCantidad() > 0);
        return this.libro;
    }

    @FXML
    private void guardar() {
        if (this.onGuardarCallback != null) {
            this.onGuardarCallback.run();
        }

        if (this.stage != null) {
            this.stage.close();
        }

    }

    @FXML
    private void cancelar() {
        if (this.stage != null) {
            this.stage.close();
        }

    }

    @FXML
    private void cerrarVentana() {
        if (this.stage != null) {
            this.stage.close();
        }

    }

    public void setOnGuardarCallback(Runnable cb) {
        this.onGuardarCallback = cb;
    }
}