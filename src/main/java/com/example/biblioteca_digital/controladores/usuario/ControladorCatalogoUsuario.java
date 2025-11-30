package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.DAO.usuario.CatalogoDAO;
import com.example.biblioteca_digital.DAO.usuario.PrestamoDAO;
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

public class ControladorCatalogoUsuario
{
    private static final String FXML_CARD_PATH = "/com/example/biblioteca_digital/vistas/usuario/Vista-Tarjeta-Libro.fxml";
    private static final List<String> GENEROS_ESTATICOS = Arrays.asList(
            "Todas", "Ficción", "Clásicos", "Ciencia Ficción", "Ciencia", "Misterio", "Fantasía"
    );

    private Usuario usuarioActual;
    private int prestamosActivos = 0;

    private final CatalogoDAO catalogoDAO = new CatalogoDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    @FXML private Label labelBienvenida;
    @FXML private Label labelContadorPrestamos;
    @FXML private FlowPane contenedorLibros;
    @FXML private TextField filtroTitulo;
    @FXML private TextField filtroAutor;
    @FXML private ChoiceBox<String> filtroGenero;

    /**
     * Metodo que establece el usuario actual y carga sus libros favoritos.
     * @param usuario El objeto Usuario actualmente logueado.
     */
    public void setUsuario(Usuario usuario)
    {
        this.usuarioActual = usuario;
        if (usuarioActual != null) {
            labelBienvenida.setText("Bienvenido, " + usuario.getNombre() + "!");
            cargarDatosIniciales();
        }if (filtroGenero != null) {
        filtroGenero.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());
    }

    }

    /**
     * Método que carga los datos iniciales de la base de datos
     */
    private void cargarDatosIniciales()
    {
        actualizarContadorPrestamos();
        if (contenedorLibros.getChildren().isEmpty())
        {
            mostrarLibrosFiltrados();
        }
    }
    /**
     * Método que inicializa el controlador con el usuario actual.
     */
    @FXML
    public void initialize()
    {
        cargarFiltroGeneros();
        if (filtroTitulo != null) filtroTitulo.textProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());
        if (filtroAutor != null) filtroAutor.textProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());
        if (filtroGenero != null)  filtroGenero.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());
    }

    /**
     * Método para actualiza los préstamos tras pedir uno ptrestado
     */
    private void actualizarContadorPrestamos()
    {
        if (usuarioActual != null)
        {
            prestamosActivos = catalogoDAO.contarPrestamosActivos(usuarioActual.getId());
            if (labelContadorPrestamos != null)
            {
                labelContadorPrestamos.setText(String.valueOf(prestamosActivos));
            }
        }
    }

    /**
     * Método para cargar los géneros
     */
    private void cargarFiltroGeneros()
    {
        if (filtroGenero != null) {
            filtroGenero.setItems(FXCollections.observableArrayList(GENEROS_ESTATICOS));
            filtroGenero.getSelectionModel().selectFirst();
        }
    }

    /**
     * Método para filtrar por los filtros de Título, Autor y Género llamando al DAO.
     */
    @FXML
    public void mostrarLibrosFiltrados() {
        if (contenedorLibros == null) return;
        contenedorLibros.getChildren().clear();

        String titulo = filtroTitulo.getText();
        String autor = filtroAutor.getText();
        String generoSeleccionado = filtroGenero.getValue();

        List<Libro> librosFiltrados = catalogoDAO.cargarCatalogo(titulo, autor, generoSeleccionado);

        for (Libro libro : librosFiltrados) {
            contenedorLibros.getChildren().add(crearVistaLibroItem(libro));
        }
    }

    //Lógica entre vistas

    /**
     * Maneja el evento de click en el botón "Pedir Prestado" de la tarjeta de libro.
     * Es llamado por el ControladorLibroCatalogo.
     * @param libro El libro que el usuario desea prestar.
     */
    public void clickPedirPrestamo(Libro libro)
    {
        if (usuarioActual == null)
        {
            mostrarAlertaError("Error de Sesión", "Debe iniciar sesión para realizar un préstamo.");
            return;
        }
        if (prestamoDAO.crearPrestamo(usuarioActual.getId(), libro.getId()))
        {
            mostrarAlerta("Préstamo Exitoso",
                    "Has pedido prestado '" + libro.getTitulo() + "'. ¡Disfruta la lectura!");

            actualizarContadorPrestamos();
            mostrarLibrosFiltrados();
        }
        else
        {
            mostrarAlertaError("Error de Préstamo",
                    "No se pudo completar el préstamo. El libro puede haberse agotado o ocurrió un error en la base de datos.");
        }
    }

    /**
     * Método para ver la cartilla de los libros individualmente ( aun no está completo)
     * @param libro
     */
    public void clickVer(Libro libro)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/biblioteca_digital/vistas/Vista-Libro-Individual.fxml"));
            Parent root = loader.load();
            ControladorLibrosIndividual controlador = loader.getController();
            controlador.setLibro(libro, usuarioActual);
            Stage stage = new Stage();
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setTitle("Detalles de: " + libro.getTitulo());
            stage.setScene(new Scene(root));
            stage.showAndWait();
        }
        catch (IOException e)
        {
            mostrarAlertaError("Error de Vista", "No se pudo cargar la vista individual del libro.");
            e.printStackTrace();
        }
    }

    /**
     * Método que maneja las mini tarjetas de libros (son individuales)
     * @param libro
     * @return
     */
    private Node crearVistaLibroItem(Libro libro)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_CARD_PATH));
            Parent item = loader.load();

            ControladorLibroCatalogo controlador = loader.getController();
            controlador.setDatos(libro, usuarioActual, this);
            return item;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return new Label("Error al cargar item: " + libro.getTitulo() + ". Verifique FXML_CARD_PATH.");
        }
    }

    /**
     * Método para alertas de información para mostrar el mensaje al usuario como una ventana
     * @param titulo
     * @param mensaje
     */
    private void mostrarAlerta(String titulo, String mensaje)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    /**
     * Método para alertas de errores para mostrar el mensaje al usuario como una ventana
     * @param titulo
     * @param mensaje
     */
    private void mostrarAlertaError(String titulo, String mensaje)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}