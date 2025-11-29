package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.modelos.Estado;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import com.example.biblioteca_digital.modelos.Prestamo;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class ControladorEditarPrestamo {

    @FXML private ComboBox<Usuario> comboUsuario;
    @FXML private ComboBox<Libro> comboLibro;
    @FXML private DatePicker fechaFin;

    private Prestamo prestamo;
    private Stage stage;
    private Runnable onGuardarCallback;

    // Listas recibidas desde el controlador principal
    private List<Usuario> usuarios;
    private List<Libro> libros;

    @FXML
    private void initialize() {
        // Mostrar SOLO el nombre del usuario
        comboUsuario.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Usuario u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? null : u.getNombreUsuario());
            }
        });
        comboUsuario.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Usuario u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? null : u.getNombreUsuario());
            }
        });

        // Mostrar SOLO el título del libro
        comboLibro.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Libro l, boolean empty) {
                super.updateItem(l, empty);
                setText(empty || l == null ? null : l.getTitulo());
            }
        });
        comboLibro.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Libro l, boolean empty) {
                super.updateItem(l, empty);
                setText(empty || l == null ? null : l.getTitulo());
            }
        });

        // Fecha mínima por defecto
        fechaFin.setValue(LocalDate.now().plusWeeks(2));
    }

    // Se llama desde el controlador principal al abrir el popup
    public void cargarDatos(List<Usuario> usuarios, List<Libro> libros) {
        this.usuarios = usuarios;
        this.libros = libros;

        comboUsuario.getItems().setAll(usuarios);
        comboLibro.getItems().setAll(libros);
    }

    public void setStage(Stage s) {
        this.stage = s;
    }

    public Prestamo getPrestamoResultado() {
        if (prestamo == null)
            prestamo = new Prestamo();

        Usuario usuarioSeleccionado = comboUsuario.getValue();
        Libro libroSeleccionado = comboLibro.getValue();

        if (usuarioSeleccionado != null)
            prestamo.setUsuario(usuarioSeleccionado);

        if (libroSeleccionado != null)
            prestamo.setLibro(libroSeleccionado);

        prestamo.setFecha_inicio(LocalDate.now());
        prestamo.setFecha_fin(fechaFin.getValue());
        prestamo.setEstado(String.valueOf(Estado.activo));

        return prestamo;
    }

    @FXML
    private void guardar() {
        if (onGuardarCallback != null)
            onGuardarCallback.run();

        if (stage != null)
            stage.close();
    }

    @FXML
    private void cancelar() {
        if (stage != null)
            stage.close();
    }

    public void setOnGuardarCallback(Runnable cb) {
        this.onGuardarCallback = cb;
    }
}