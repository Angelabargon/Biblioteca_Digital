package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.dao.CatalogoDAO; // Nuevo DAO
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ControladorCatalogoUsuario {

    // --- Géneros Estáticos ---
    private static final List<String> GENEROS_ESTATICOS = Arrays.asList(
            "Todas", "Ficción", "Clásicos", "Ciencia Ficción", "Ciencia", "Misterio", "Fantasía"
    );

    // --- Atributos de estado ---
    private Usuario usuarioActual;
    private List<Libro> libros = new ArrayList<>();
    private int prestamosActivos = 0;

    // --- Instancias DAO ---
    private final CatalogoDAO catalogoDAO = new CatalogoDAO();

    // --- Elementos FXML ---
    @FXML private Label labelBienvenida;
    @FXML private Label labelContadorPrestamos;
    @FXML private FlowPane contenedorLibros;
    @FXML private TextField filtroTitulo;
    @FXML private TextField filtroAutor;
    @FXML private ChoiceBox<String> filtroGenero;

    @FXML
    public void initialize() {
        // Enlazar listeners para el filtrado automático
        if (filtroTitulo != null) filtroTitulo.textProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());
        if (filtroAutor != null) filtroAutor.textProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());
        if (filtroGenero != null) filtroGenero.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());

        cargarFiltroGeneros(); // Cargar los géneros estáticos al inicializar
    }

    /**
     * Establece el usuario y carga los datos iniciales.
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;

        if (labelBienvenida != null) labelBienvenida.setText("Bienvenido, " + usuario.getNombre());

        // Cargar datos usando DAO
        libros = catalogoDAO.obtenerTodosLosLibros();
        prestamosActivos = catalogoDAO.contarPrestamosActivos(usuario.getId());

        // Actualizar contadores
        if (labelContadorPrestamos != null) labelContadorPrestamos.setText(String.valueOf(prestamosActivos));

        mostrarLibrosFiltrados();
    }

    private void cargarFiltroGeneros() {
        if (filtroGenero != null) {
            filtroGenero.setItems(FXCollections.observableArrayList(GENEROS_ESTATICOS));
            filtroGenero.getSelectionModel().selectFirst();
        }
    }


    // --- LÓGICA DE FILTRADO Y VISUALIZACIÓN ---

    /**
     * Aplica los filtros de Título, Autor y Género.
     */
    @FXML
    public void mostrarLibrosFiltrados() {
        if (contenedorLibros == null) return;
        contenedorLibros.getChildren().clear();

        // Obtener valores de filtro (se aplica toLowerCase para la búsqueda)
        String titulo = filtroTitulo.getText() != null ? filtroTitulo.getText().toLowerCase() : "";
        String autor = filtroAutor.getText() != null ? filtroAutor.getText().toLowerCase() : "";
        String generoSeleccionado = filtroGenero.getValue() != null && !filtroGenero.getValue().equals("Todas")
                ? filtroGenero.getValue().toLowerCase() : "";

        for (Libro libro : libros) {
            String libroTitulo = libro.getTitulo() != null ? libro.getTitulo().toLowerCase() : "";
            String libroAutor = libro.getAutor() != null ? libro.getAutor().toLowerCase() : "";
            String libroGenero = libro.getGenero() != null ? libro.getGenero().toLowerCase() : "";

            boolean coincide = libroTitulo.contains(titulo)
                    && libroAutor.contains(autor)
                    && (generoSeleccionado.isEmpty() || libroGenero.equals(generoSeleccionado));

            if (coincide) {
                contenedorLibros.getChildren().add(crearVistaLibroItem(libro));
            }
        }
    }

    // --- LÓGICA DE INTERACCIÓN DE TARJETAS ---

    /**
     * Método llamado por el ControladorLibroCatalogo para pedir un préstamo.
     */
    public void clickPedirPrestamo(Libro libro) {
        // La lógica de verificar disponibilidad ya está dentro de CatalogoDAO.pedirPrestado
        if (catalogoDAO.pedirPrestado(usuarioActual.getId(), libro.getId())) {
            mostrarAlerta("Préstamo Exitoso", "Has solicitado el préstamo de " + libro.getTitulo() + ".");

            // Refrescar datos
            libros = catalogoDAO.obtenerTodosLosLibros(); // Recargar para actualizar disponibles
            prestamosActivos = catalogoDAO.contarPrestamosActivos(usuarioActual.getId());
            if (labelContadorPrestamos != null) labelContadorPrestamos.setText(String.valueOf(prestamosActivos));

            mostrarLibrosFiltrados(); // Actualizar UI
        } else {
            // Este error puede ser por límite del usuario o por disponibilidad (DAO lo verifica)
            mostrarAlertaError("Préstamo Fallido", "No se pudo solicitar el préstamo. Revisa si el libro está disponible o si has alcanzado tu límite de préstamos.");
        }
    }

    /**
     * Método llamado por el ControladorLibroCatalogo para ver detalles.
     */
    public void clickVer(Libro libro) {
        // ... (Lógica para abrir la vista individual)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Libro-Usuario.fxml"));
            Parent root = loader.load();

            ControladorLibrosIndividual controlador = loader.getController();
            controlador.setLibro(libro, usuarioActual);

            Stage stage = new Stage();
            stage.setTitle(libro.getTitulo());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlertaError("Error de Carga", "No se pudo cargar la vista de detalle del libro.");
        }
    }

    // --- LÓGICA DE CARGA DE VISTAS (Item del Libro) ---

    private Node crearVistaLibroItem(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Libro-Item.fxml"));
            Parent item = loader.load();

            ControladorLibroCatalogo controlador = loader.getController();

            // Se asume la siguiente firma para la tarjeta de libro:
            // El controlador de la tarjeta necesita el libro y la referencia al controlador padre para los callbacks.
            controlador.setDatos(libro, usuarioActual, this);

            return item;
        } catch (IOException e) {
            e.printStackTrace();
            return new Label("Error al cargar item: " + libro.getTitulo());
        }
    }

    // --- UTILIDADES ---
    private void mostrarAlerta(String titulo, String mensaje) {
        // ... (Tu lógica de alerta)
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        // ... (Tu lógica de alerta de error)
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}