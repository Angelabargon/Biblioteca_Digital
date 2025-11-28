package com.example.biblioteca_digital.controladores.usuario;

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

public class ControladorCatalogoUsuario
{

    private static final List<String> GENEROS_ESTATICOS = Arrays.asList(
            "Todas", "Ficción", "Clásicos", "Ciencia Ficción", "Ciencia", "Misterio", "Fantasía"
    );

    private Usuario usuarioActual;
    private int prestamosActivos = 0;

    private final CatalogoDAO catalogoDAO = new CatalogoDAO();

    @FXML private Label labelBienvenida;
    @FXML private Label labelContadorPrestamos;
    @FXML private FlowPane contenedorLibros;
    @FXML private TextField filtroTitulo;
    @FXML private TextField filtroAutor;
    @FXML private ChoiceBox<String> filtroGenero;

    @FXML
    public void initialize()
    {
        if (filtroTitulo != null) filtroTitulo.textProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());
        if (filtroAutor != null) filtroAutor.textProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());
        if (filtroGenero != null) filtroGenero.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());

        cargarFiltroGeneros(); // Cargar los géneros estáticos al inicializar
    }

    /**
     * Establece el usuario y carga los datos iniciales.
     */
    public void setUsuario(Usuario usuario)
    {
        this.usuarioActual = usuario;

        if (labelBienvenida != null) labelBienvenida.setText("Bienvenido, " + usuario.getNombre());

        // Cargar prestamos activos
        prestamosActivos = catalogoDAO.contarPrestamosActivos(usuario.getId());

        // Actualizar contadores
        if (labelContadorPrestamos != null) labelContadorPrestamos.setText(String.valueOf(prestamosActivos));

        // Cargar el catálogo completo por primera vez (o filtrado si hay valores iniciales)
        mostrarLibrosFiltrados();
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

        // **USO DEL DAO PARA FILTRAR EN LA BASE DE DATOS**
        List<Libro> librosFiltrados = catalogoDAO.cargarCatalogo(titulo, autor, generoSeleccionado);

        for (Libro libro : librosFiltrados) {
            contenedorLibros.getChildren().add(crearVistaLibroItem(libro));
        }
    }

    // --- LÓGICA DE INTERACCIÓN DE TARJETAS ---

    /**
     * Método llamado por el ControladorLibroCatalogo para pedir un préstamo.
     */
    public void clickPedirPrestamo(Libro libro) {
        if (catalogoDAO.pedirPrestado(usuarioActual.getId(), libro.getId())) {
            mostrarAlerta("Préstamo Exitoso", "Has solicitado el préstamo de " + libro.getTitulo() + ".");

            // 1. Refrescar el contador de préstamos
            prestamosActivos = catalogoDAO.contarPrestamosActivos(usuarioActual.getId());
            if (labelContadorPrestamos != null) labelContadorPrestamos.setText(String.valueOf(prestamosActivos));

            // 2. Refrescar la vista del catálogo (llama al DAO para recargar los libros con stock actualizado)
            mostrarLibrosFiltrados();
        } else {
            // Este error puede ser por límite del usuario o por disponibilidad (DAO lo verifica)
            mostrarAlertaError("Préstamo Fallido", "No se pudo solicitar el préstamo. Revisa si el libro está disponible o si has alcanzado tu límite de préstamos.");
        }
    }

    // El resto de la lógica de vistas y alertas se mantiene igual...
    public void clickVer(Libro libro)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/biblioteca_digital/vistas/Vista-Libro-Individual.fxml"));

            Parent root = loader.load();
            // Asegúrate de que ControladorLibrosIndividual existe y tiene setLibro
            // Si el nombre del controlador en el recurso FXML es diferente, corrígelo
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/usuario/Vista-Libro-Item.fxml"));
            Parent item = loader.load();

            ControladorLibroCatalogo controlador = loader.getController();
            controlador.setDatos(libro, usuarioActual, this);
            return item;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return new Label("Error al cargar item: " + libro.getTitulo());
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