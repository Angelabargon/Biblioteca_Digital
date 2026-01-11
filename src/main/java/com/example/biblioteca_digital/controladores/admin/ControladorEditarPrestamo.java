package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Prestamo;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

public class ControladorEditarPrestamo {

    @FXML private Label lblTitulo;
    @FXML private Button btnGuardar;

    @FXML private ComboBox<Usuario> comboUsuario;
    @FXML private ComboBox<Libro> comboLibro;
    @FXML private DatePicker fechaFin;
    @FXML private ComboBox<String> comboEstado;

    private Prestamo prestamoEditar;
    private Stage stage;
    private Runnable onGuardarCallback;

    /* ===============================
       INIT
       =============================== */
    @FXML
    public void initialize() {

        // ❌ Eliminado "devuelto"
        comboEstado.getItems().addAll("activo", "bloqueado");

        comboUsuario.setConverter(new StringConverter<>() {
            @Override
            public String toString(Usuario u) {
                return u == null ? "" : u.getNombreUsuario();
            }
            @Override public Usuario fromString(String s) { return null; }
        });

        comboLibro.setConverter(new StringConverter<>() {
            @Override
            public String toString(Libro l) {
                return l == null ? "" : l.getTitulo();
            }
            @Override public Libro fromString(String s) { return null; }
        });
    }

    /* ===============================
       CARGA LISTAS
       =============================== */
    public void cargarDatos(List<Usuario> usuarios, List<Libro> libros) {
        comboUsuario.getItems().setAll(usuarios);
        comboLibro.getItems().setAll(libros);
    }

    /* ===============================
       MODO CREAR
       =============================== */
    public void prepararNuevoPrestamo() {
        lblTitulo.setText("Nuevo Préstamo");
        btnGuardar.setText("Crear Préstamo");
        prestamoEditar = new Prestamo();
    }

    /* ===============================
       MODO EDITAR
       =============================== */
    public void setPrestamoEditar(Prestamo p) {
        this.prestamoEditar = p;

        lblTitulo.setText("Editar Préstamo");
        btnGuardar.setText("Guardar Cambios");

        comboUsuario.setValue(p.getUsuario());
        comboLibro.setValue(p.getLibro());
        fechaFin.setValue(p.getFecha_fin());

        // Si viene como "devuelto", no se puede editar → se fuerza a activo
        if ("devuelto".equals(p.getEstado())) {
            comboEstado.setValue("activo");
        } else {
            comboEstado.setValue(p.getEstado());
        }

        comboUsuario.setDisable(true);
        comboLibro.setDisable(true);
    }

    /* ===============================
       GUARDAR
       =============================== */
    @FXML
    private void guardar() {

        if (fechaFin.getValue() == null || comboEstado.getValue() == null) {
            new Alert(Alert.AlertType.ERROR, "Todos los campos son obligatorios").show();
            return;
        }

        prestamoEditar.setFecha_fin(fechaFin.getValue());
        prestamoEditar.setEstado(comboEstado.getValue());

        if (onGuardarCallback != null) {
            onGuardarCallback.run();
        }

        stage.close();
    }

    @FXML
    private void cancelar() {
        stage.close();
    }

    /* ===============================
       HELPERS
       =============================== */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOnGuardarCallback(Runnable cb) {
        this.onGuardarCallback = cb;
    }

    public Prestamo getPrestamoResultado() {
        return prestamoEditar;
    }
}
