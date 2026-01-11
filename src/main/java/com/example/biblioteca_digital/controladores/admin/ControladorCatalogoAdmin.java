package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.DAO.usuario.CatalogoDAO;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Controlador del catálogo de libros en el panel de administración.
 *
 * Se encarga de mostrar el catálogo completo de libros en formato de tarjetas,
 * permitiendo al administrador filtrar por título, autor y género,
 * así como acceder a la vista detallada de cada libro.
 */
public class ControladorCatalogoAdmin {

    /** Ruta al archivo FXML que representa una tarjeta individual de libro. */
    private static final String FXML_CARD_PATH =
            "/com/example/biblioteca_digital/vistas/admin/VistaTarjetaLibroAdmin.fxml";

    /**
     * Lista estática de géneros disponibles para el filtro.
     * Incluye la opción "Todas" para mostrar el catálogo completo.
     */
    private static final List<String> GENEROS_ESTATICOS = Arrays.asList(
            "Todas", "Ficción", "Clásicos", "Tragedia", "Terror",
            "Romance", "Ciencia Ficción", "Ciencia", "Misterio", "Fantasía"
    );

    /** Usuario administrador actualmente autenticado. */
    private Usuario usuarioActual;

    /** DAO encargado de obtener los libros del catálogo desde la base de datos. */
    private final CatalogoDAO catalogoDAO = new CatalogoDAO();

    /** Etiqueta de bienvenida que muestra el nombre del administrador. */
    @FXML private Label labelBienvenida;

    /** Contenedor visual donde se cargan las tarjetas de libros. */
    @FXML private FlowPane contenedorLibros;

    /** Campo de texto para filtrar libros por título. */
    @FXML private TextField filtroTitulo;

    /** Campo de texto para filtrar libros por autor. */
    @FXML private TextField filtroAutor;

    /** Selector de género para filtrar los libros del catálogo. */
    @FXML private ChoiceBox<String> filtroGenero;

    /**
     * Asigna el usuario administrador actual al controlador.
     * Actualiza el mensaje de bienvenida y carga el catálogo inicial.
     *
     * @param usuario Usuario administrador autenticado.
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;

        if (labelBienvenida != null && usuario != null) {
            labelBienvenida.setText("Administrador: " + usuario.getNombre());
        }

        cargarFiltroGeneros();
        mostrarLibrosFiltrados();
    }

    /**
     * Método de inicialización del controlador.
     * Configura los filtros y añade los listeners necesarios
     * para actualizar el catálogo en tiempo real.
     */
    @FXML
    public void initialize() {

        cargarFiltroGeneros();
        mostrarLibrosFiltrados();

        if (filtroTitulo != null)
            filtroTitulo.textProperty().addListener(
                    (obs, oldV, newV) -> mostrarLibrosFiltrados()
            );

        if (filtroAutor != null)
            filtroAutor.textProperty().addListener(
                    (obs, oldV, newV) -> mostrarLibrosFiltrados()
            );

        if (filtroGenero != null)
            filtroGenero.getSelectionModel()
                    .selectedItemProperty()
                    .addListener(
                            (obs, oldV, newV) -> mostrarLibrosFiltrados()
                    );
    }

    /**
     * Carga los géneros disponibles en el selector de filtros
     * y selecciona por defecto la opción "Todas".
     */
    private void cargarFiltroGeneros() {
        filtroGenero.setItems(
                FXCollections.observableArrayList(GENEROS_ESTATICOS)
        );
        filtroGenero.getSelectionModel().selectFirst();
    }

    /**
     * Obtiene los libros filtrados desde el DAO y
     * actualiza visualmente el contenedor de tarjetas.
     */
    @FXML
    public void mostrarLibrosFiltrados() {

        contenedorLibros.getChildren().clear();

        List<Libro> libros = catalogoDAO.cargarCatalogo(
                filtroTitulo.getText(),
                filtroAutor.getText(),
                filtroGenero.getValue()
        );

        for (Libro libro : libros) {
            contenedorLibros.getChildren().add(
                    crearVistaLibroItem(libro)
            );
        }
    }

    /**
     * Crea la vista gráfica de una tarjeta de libro a partir de su FXML.
     *
     * @param libro Libro cuyos datos se mostrarán en la tarjeta.
     * @return Nodo JavaFX que representa la tarjeta del libro.
     */
    private Node crearVistaLibroItem(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(FXML_CARD_PATH)
            );
            Parent item = loader.load();

            ControladorLibroCatalogoAdmin controlador =
                    loader.getController();
            controlador.setDatos(libro, this);

            return item;

        } catch (IOException e) {
            e.printStackTrace();
            return new Label("Error cargando libro");
        }
    }

    /**
     * Abre la vista detallada de un libro en una ventana modal.
     *
     * @param libro Libro seleccionado por el administrador.
     */
    public void clickVer(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/example/biblioteca_digital/vistas/admin/VistaLibroIndividualAdmin.fxml"
                    )
            );
            Parent root = loader.load();

            ControladorLibroIndividualAdmin controlador =
                    loader.getController();
            controlador.setLibro(libro);

            Stage stage = new Stage();
            stage.setTitle(libro.getTitulo());
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}