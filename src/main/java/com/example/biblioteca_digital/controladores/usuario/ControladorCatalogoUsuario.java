package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.DAO.usuario.CatalogoDAO;
import com.example.biblioteca_digital.DAO.usuario.FavoritosDAO;
import com.example.biblioteca_digital.DAO.usuario.PrestamoDAO;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Prestamo;
import com.example.biblioteca_digital.modelos.Sesion;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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
     * Establece el usuario y carga los datos iniciales.
     * Este método debe ser llamado por el controlador padre después de cargar el FXML.
     */
    public void setUsuario(Usuario usuario)
    {
        this.usuarioActual = usuario;
        if (usuarioActual == null)
        {
            System.err.println("Error: setUsuario fue llamado con un objeto Usuario nulo.");
            return;
        }

        if (labelBienvenida != null)
        {
            labelBienvenida.setText("Bienvenido, " + usuario.getNombre());
        }

        actualizarContadorPrestamos();
        mostrarLibrosFiltrados();
    }

    @FXML
    public void initialize()
    {
        if (filtroTitulo != null) filtroTitulo.textProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());
        if (filtroAutor != null) filtroAutor.textProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());
        if (filtroGenero != null) filtroGenero.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());

        cargarFiltroGeneros(); // Cargar los géneros estáticos al inicializar
    }

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

    private void cargarFiltroGeneros()
    {
        if (filtroGenero != null) {
            filtroGenero.setItems(FXCollections.observableArrayList(GENEROS_ESTATICOS));
            filtroGenero.getSelectionModel().selectFirst();
        }
    }

    /**
     * Aplica los filtros de Título, Autor y Género llamando al DAO.
     */
    @FXML
    public void mostrarLibrosFiltrados() {
        if (contenedorLibros == null) return;
        contenedorLibros.getChildren().clear();

        // Obtener valores de filtro (ya no es necesario toLowerCase aquí, el DAO lo maneja en SQL)
        String titulo = filtroTitulo.getText();
        String autor = filtroAutor.getText();
        String generoSeleccionado = filtroGenero.getValue();

        // Uso del dao para filtrar en la bd
        List<Libro> librosFiltrados = catalogoDAO.cargarCatalogo(titulo, autor, generoSeleccionado);

        for (Libro libro : librosFiltrados) {
            contenedorLibros.getChildren().add(crearVistaLibroItem(libro));
        }
    }

    //Lógica entre vistas

    /**
     * Método llamado por el ControladorLibroCatalogo para pedir un préstamo.
     */
    public void registrarPrestamo(Libro libro, Usuario usuario)
    {
        Prestamo nuevoPrestamo = new Prestamo(usuario.getId(), libro.getId());
        if (prestamoDAO.guardarPrestamo(nuevoPrestamo))
        {
            mostrarAlerta("Préstamo Exitoso", "Has tomado prestado el libro: " + libro.getTitulo());
            actualizarContadorPrestamos();
            mostrarLibrosFiltrados();
        }
        else
        {
            mostrarAlertaError("Error de Préstamo", "No se pudo registrar el préstamo. Intente de nuevo.");
        }
    }
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
                    "Has prestado '" + libro.getTitulo() + "'. ¡Disfruta la lectura!");

            actualizarContadorPrestamos();
            mostrarLibrosFiltrados();
        }
        else
        {
            mostrarAlertaError("Error de Préstamo",
                    "No se pudo completar el préstamo. El libro puede haberse agotado o ocurrió un error en la base de datos.");
        }
    }

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

    private void mostrarAlerta(String titulo, String mensaje)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAlertaError(String titulo, String mensaje)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}