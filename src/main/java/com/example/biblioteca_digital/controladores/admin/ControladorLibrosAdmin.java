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

/**
 * Controlador encargado de gestionar el módulo de administración de libros.
 * Permite listar, buscar, agregar, editar y eliminar libros dentro del sistema.
 *
 * Este controlador interactúa con {@link LibroAdminDAO} para obtener y modificar datos,
 * y utiliza varias celdas personalizadas para mejorar la visualización en la tabla.
 */
public class ControladorLibrosAdmin {

    /** Tabla principal donde se muestran los libros disponibles. */
    @FXML private TableView<Libro> tablaLibros;

    /** Columna que muestra el título del libro. */
    @FXML private TableColumn<Libro, String> colTitulo;

    /** Columna que muestra el autor del libro. */
    @FXML private TableColumn<Libro, String> colAutor;

    /** Columna que muestra el género del libro. */
    @FXML private TableColumn<Libro, String> colGenero;

    /** Columna que muestra el ISBN del libro. */
    @FXML private TableColumn<Libro, String> colIsbn;

    /** Columna que muestra la cantidad total disponible del libro. */
    @FXML private TableColumn<Libro, Integer> colCantidad;

    /** Columna que muestra si el libro está disponible o no. */
    @FXML private TableColumn<Libro, Boolean> colEstado;

    /** Columna que contiene los botones de acciones (editar, eliminar). */
    @FXML private TableColumn<Libro, Void> colAcciones;

    /** Campo de texto para realizar búsquedas en tiempo real. */
    @FXML private TextField txtBuscar;

    /** Servicio de acceso a datos para la gestión de libros. */
    private final LibroAdminDAO libroServicio = new LibroAdminDAO();

    /** Lista observable utilizada para poblar la tabla de libros. */
    private final ObservableList<Libro> lista = FXCollections.observableArrayList();

    /**
     * Inicializa la vista configurando columnas, cell factories y cargando los libros.
     */
    @FXML
    public void initialize() {

        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("disponible"));

        configurarGeneroCellFactory();
        configurarEstadoCellFactory();
        configurarAccionesCellFactory();

        cargarLibros();

        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> buscarLibro());
        }
    }

    /**
     * Configura la celda de la columna “género” para mostrar estilos personalizados.
     */
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
    }

    /**
     * Configura la celda del estado de disponibilidad,
     * mostrando etiquetas verdes o rojas según corresponda.
     */
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

    /**
     * Configura la columna de acciones, agregando los botones de editar y eliminar.
     */
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

    /**
     * Carga todos los libros desde la base de datos y los muestra en la tabla.
     */
    private void cargarLibros() {
        lista.setAll(libroServicio.obtenerTodos());
        tablaLibros.setItems(lista);
    }

    /**
     * Realiza una búsqueda filtrando por título, autor, género o ISBN.
     */
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

    /**
     * Abre el editor en modo "agregar libro".
     */
    @FXML
    public void abrirAgregarLibro() { abrirEditor(null); }

    /**
     * Abre el editor para el libro seleccionado.
     * Muestra una alerta si no se seleccionó ninguno.
     */
    @FXML
    public void editarLibro() {
        Libro l = tablaLibros.getSelectionModel().getSelectedItem();
        if (l == null) {
            mostrarAlerta("Selecciona un libro.");
            return;
        }
        abrirEditor(l);
    }

    /**
     * Abre la ventana de edición/creación de libros, configurando los callbacks
     * para guardar los cambios en la base de datos.
     *
     * @param libro libro a editar, o null para crear uno nuevo.
     */
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

    /**
     * Elimina el libro seleccionado de la tabla.
     * Solicita confirmación antes de proceder.
     */
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

    /**
     * Elimina un libro directamente desde el botón de acciones dentro de la tabla.
     *
     * @param libro libro a eliminar
     */
    private void eliminarLibroDirecto(Libro libro) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar \"" + libro.getTitulo() + "\"?",
                ButtonType.OK, ButtonType.CANCEL);

        if (a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            libroServicio.eliminarLibro(libro.getId());
            cargarLibros();
        }
    }

    /**
     * Muestra una alerta de advertencia con el mensaje indicado.
     *
     * @param msg Mensaje a mostrar.
     */
    private void mostrarAlerta(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}

