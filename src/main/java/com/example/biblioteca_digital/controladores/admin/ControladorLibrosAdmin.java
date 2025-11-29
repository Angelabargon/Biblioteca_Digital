package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.DAO.admin.LibroAdminDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

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

        // Value factories — columnas de la tabla
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("disponible"));

        // Cell factories visuales
        configurarGeneroCellFactory();
        configurarEstadoCellFactory();
        configurarAccionesCellFactory();

        cargarLibros();
    }

    // -----------------------------
    //   CELL FACTORY: GÉNERO
    // -----------------------------
    private void configurarGeneroCellFactory() {
        colGenero.setCellFactory(col -> new TableCell<Libro, String>() {
            @Override
            protected void updateItem(String genero, boolean empty) {
                super.updateItem(genero, empty);
                if (empty || genero == null) {
                    setGraphic(null);
                    return;
                }
                Label tag = new Label(genero);
                tag.setStyle("-fx-background-color: #c99b68; -fx-text-fill: white; " +
                        "-fx-padding: 4 10; -fx-background-radius: 10;");
                setGraphic(tag);
            }
        });

        // listener búsqueda en tiempo real
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> buscarLibro());
        }
    }

    // -----------------------------
    //   CELL FACTORY: ESTADO
    // -----------------------------
    private void configurarEstadoCellFactory() {
        colEstado.setCellFactory(col -> new TableCell<Libro, Boolean>() {
            @Override
            protected void updateItem(Boolean disponible, boolean empty) {
                super.updateItem(disponible, empty);

                if (empty || disponible == null) {
                    setGraphic(null);
                    return;
                }

                Label badge = new Label(disponible ? "Disponible" : "No disponible");
                badge.setStyle(
                        disponible
                                ? "-fx-background-color: #10B981; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 8;"
                                : "-fx-background-color: #ef4444; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 8;"
                );
                setGraphic(badge);
            }
        });
    }

    // -----------------------------
    //   CELL FACTORY: ACCIONES
    // -----------------------------
    private void configurarAccionesCellFactory() {
        colAcciones.setCellFactory(col -> new TableCell<Libro, Void>() {

            private final Button btnEditar = new Button("\u270E");
            private final Button btnEliminar = new Button("\uD83D\uDDD1");
            private final HBox contenedor = new HBox(8);

            {
                // Botón Editar
                btnEditar.setStyle("-fx-background-color: #fff6ee; -fx-border-radius: 8; -fx-background-radius: 8;");
                btnEditar.setOnAction(e -> {
                    Libro libro = getTableView().getItems().get(getIndex());
                    abrirEditor(libro);
                });

                // Botón Eliminar
                btnEliminar.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white;" +
                        " -fx-border-radius: 8; -fx-background-radius: 8;");
                btnEliminar.setOnAction(e -> {
                    Libro libro = getTableView().getItems().get(getIndex());
                    eliminarLibroDirecto(libro);
                });

                contenedor.setPadding(new Insets(4,0,4,0));
                contenedor.getChildren().addAll(btnEditar, btnEliminar);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : contenedor);
            }
        });
    }

    // -----------------------------
    //   CARGAR LIBROS
    // -----------------------------
    private void cargarLibros() {
        lista.setAll(libroServicio.obtenerTodos());
        tablaLibros.setItems(lista);
    }

    // -----------------------------
    //   BUSCAR LIBRO
    // -----------------------------
    @FXML
    private void buscarLibro() {
        String q = txtBuscar.getText().trim().toLowerCase();
        if (q.isEmpty()) {
            cargarLibros();
            return;
        }

        ObservableList<Libro> filtrado = lista.filtered(
                l ->
                        (l.getTitulo() != null && l.getTitulo().toLowerCase().contains(q))
                                ||  (l.getAutor() != null && l.getAutor().toLowerCase().contains(q))
                                ||  (l.getGenero() != null && l.getGenero().toLowerCase().contains(q))
                                ||  (l.getIsbn()   != null && l.getIsbn().toLowerCase().contains(q))
        );

        tablaLibros.setItems(filtrado);
    }

    // -----------------------------
    //   ABRIR EDITOR
    // -----------------------------
    @FXML
    public void abrirAgregarLibro() { abrirEditor(null); }

    @FXML
    public void editarLibro() {
        Libro l = tablaLibros.getSelectionModel().getSelectedItem();
        if (l == null) {
            mostrarAlerta("Selecciona un libro.");
            return;
        }
        abrirEditor(l);
    }

    private void abrirEditor(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/biblioteca_digital/vistas/admin/editarLibro.fxml"
            ));

            Parent root = loader.load();
            ControladorEditarLibros ctrl = loader.getController();

            Stage ventana = new Stage();
            ventana.initOwner(tablaLibros.getScene().getWindow());
            ventana.initModality(Modality.APPLICATION_MODAL);
            ventana.setScene(new Scene(root));

            ctrl.setStage(ventana);
            if (libro != null) ctrl.setLibro(libro);

            ctrl.setOnGuardarCallback(() -> {
                Libro resultado = ctrl.getLibroResultado();

                if (libro == null)
                    libroServicio.agregarLibro(resultado);
                else {
                    resultado.setId(libro.getId());
                    libroServicio.actualizarLibro(resultado);
                }

                cargarLibros();
            });

            ventana.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al abrir el editor.");
        }
    }

    // -----------------------------
    //   ELIMINAR LIBROS
    // -----------------------------
    @FXML
    public void eliminarLibro() {
        Libro l = tablaLibros.getSelectionModel().getSelectedItem();
        if (l == null) {
            mostrarAlerta("Selecciona un libro.");
            return;
        }

        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar libro?", ButtonType.OK, ButtonType.CANCEL);

        if (a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            libroServicio.eliminarLibro(l.getId());
            cargarLibros();
        }
    }

    private void eliminarLibroDirecto(Libro libro) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar \"" + libro.getTitulo() + "\"?",
                ButtonType.OK, ButtonType.CANCEL);

        if (a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            libroServicio.eliminarLibro(libro.getId());
            cargarLibros();
        }
    }

    // -----------------------------
    //   UTILIDAD
    // -----------------------------
    private void mostrarAlerta(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}

