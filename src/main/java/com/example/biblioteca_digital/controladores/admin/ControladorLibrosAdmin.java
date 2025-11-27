package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.DAO.admin.LibroAdminDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * Controlador para la vista adminLibros.fxml
 * - Añade cell factories para categoría (tag), estado (badge) y acciones (editar/eliminar).
 * - Usa el DAO LibroAdminDAO que ya tienes.
 */
public class ControladorLibrosAdmin {

    @FXML private TableView<Libro> tablaLibros;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, String> colGenero;
    @FXML private TableColumn<Libro, String> colIsbn;
    @FXML private TableColumn<Libro, Integer> colCantidad;
    @FXML private TableColumn<Libro, Boolean> colEstado;
    @FXML private TableColumn<Libro, Void> colAcciones;

    @FXML private TextField txtBuscar;

    private final LibroAdminDAO libroServicio = new LibroAdminDAO();
    private final ObservableList<Libro> lista = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Seteamos value factories (propiedades del modelo)
        if (colTitulo != null) colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        if (colAutor != null) colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        if (colGenero != null) colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        if (colIsbn != null) colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        if (colCantidad != null) colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        if (colEstado != null) colEstado.setCellValueFactory(new PropertyValueFactory<>("disponible"));

        // Cell factories visuales
        if (colGenero != null) configurarGeneroCellFactory();
        if (colEstado != null) configurarEstadoCellFactory();
        if (colAcciones != null) configurarAccionesCellFactory();

        cargarLibros();
    }

    private void configurarGeneroCellFactory() {
        colGenero.setCellFactory(col -> new TableCell<Libro, String>() {
            @Override
            protected void updateItem(String genero, boolean empty) {
                super.updateItem(genero, empty);
                if (empty || genero == null || genero.isEmpty()) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label tag = new Label(genero);
                    // estilo parecido al Figma (marrón claro, bordes redondeados)
                    tag.setStyle("-fx-background-color: #c99b68; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 10;");
                    setGraphic(tag);
                    setText(null);
                }
            }
        });
    }

    private void configurarEstadoCellFactory() {
        colEstado.setCellFactory(col -> new TableCell<Libro, Boolean>() {
            @Override
            protected void updateItem(Boolean disponible, boolean empty) {
                super.updateItem(disponible, empty);
                if (empty || disponible == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label(disponible ? "Disponible" : "No disponible");
                    if (disponible) {
                        badge.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 8;");
                    } else {
                        badge.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 8;");
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });
    }

    private void configurarAccionesCellFactory() {
        colAcciones.setCellFactory(col -> new TableCell<Libro, Void>() {

            private final Button btnEditar = new Button();
            private final Button btnEliminar = new Button();
            private final HBox contenedor = new HBox(8);

            {
                // Botón editar (icono textual por ahora)
                btnEditar.setText("\u270E"); // ✎
                btnEditar.setStyle("-fx-background-color: #fff6ee; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding:6;");
                btnEditar.setOnAction(e -> {
                    Libro libro = getTableView().getItems().get(getIndex());
                    if (libro != null) abrirEditor(libro);
                });

                // Botón eliminar
                btnEliminar.setText("\uD83D\uDDD1"); // 🗑 (dependiendo de la fuente puede variar)
                btnEliminar.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding:6;");
                btnEliminar.setOnAction(e -> {
                    Libro libro = getTableView().getItems().get(getIndex());
                    if (libro != null) eliminarLibroDirecto(libro);
                });

                contenedor.setPadding(new Insets(4,0,4,0));
                contenedor.getChildren().addAll(btnEditar, btnEliminar);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(contenedor);
                }
            }
        });
    }

    // Cargar todos los libros desde el DAO
    private void cargarLibros() {
        lista.setAll(libroServicio.obtenerTodos());
        if (tablaLibros != null) tablaLibros.setItems(lista);
    }

    @FXML
    public void buscarLibro() {
        String q = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        if (q.isEmpty()) { cargarLibros(); return; }
        ObservableList<Libro> filt = lista.filtered(l ->
                (l.getTitulo()!=null && l.getTitulo().toLowerCase().contains(q)) ||
                        (l.getAutor()!=null && l.getAutor().toLowerCase().contains(q)) ||
                        (l.getGenero()!=null && l.getGenero().toLowerCase().contains(q)) ||
                        (l.getIsbn()!=null && l.getIsbn().toLowerCase().contains(q))
        );
        if (tablaLibros != null) tablaLibros.setItems(filt);
    }

    // Método original para abrir editor (ya lo tenías)
    @FXML public void abrirAgregarLibro() { abrirEditor(null); }

    @FXML public void editarLibro() {
        Libro sel = tablaLibros.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Selecciona un libro"); return; }
        abrirEditor(sel);
    }

    @FXML public void eliminarLibro() {
        // Este método se mantiene para el botón inferior que tenías
        Libro sel = tablaLibros.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Selecciona un libro"); return; }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Eliminar libro?", ButtonType.OK, ButtonType.CANCEL);
        a.setHeaderText(null);
        Optional<ButtonType> r = a.showAndWait();
        if (r.isPresent() && r.get()==ButtonType.OK) {
            boolean ok = libroServicio.eliminarLibro(sel.getId());
            if (!ok) mostrarAlerta("No se pudo eliminar (referencias).");
            cargarLibros();
        }
    }

    /**
     * Eliminación invocada por el botón de la celda (sin usar selección)
     */
    private void eliminarLibroDirecto(Libro libro) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Eliminar libro \"" + libro.getTitulo() + "\"?", ButtonType.OK, ButtonType.CANCEL);
        a.setHeaderText(null);
        Optional<ButtonType> r = a.showAndWait();
        if (r.isPresent() && r.get()==ButtonType.OK) {
            boolean ok = libroServicio.eliminarLibro(libro.getId());
            if (!ok) mostrarAlerta("No se pudo eliminar (referencias).");
            cargarLibros();
        }
    }

    /**
     * Abre el editor (reusa tu implementación). Se pasa el libro seleccionado si se edita.
     */
    private void abrirEditor(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/admin/editarLibro.fxml"));
            Parent root = loader.load();
            // Tu controlador del editor: ControladorEditarLibros
            com.example.biblioteca_digital.controladores.admin.ControladorEditarLibros ctrl = loader.getController();
            Stage st = new Stage();
            st.initOwner(tablaLibros.getScene().getWindow());
            st.initModality(Modality.APPLICATION_MODAL);
            ctrl.setStage(st);
            if (libro != null) ctrl.setLibro(libro);
            ctrl.setOnGuardarCallback(() -> {
                Libro res = ctrl.getLibroResultado();
                if (libro==null) libroServicio.agregarLibro(res);
                else { res.setId(libro.getId()); libroServicio.actualizarLibro(res); }
                cargarLibros();
            });
            st.setScene(new javafx.scene.Scene(root));
            st.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al abrir editor");
        }
    }

    private void mostrarAlerta(String t) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(t);
        a.showAndWait();
    }
}



