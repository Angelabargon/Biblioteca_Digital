package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ControladorCatalogoUsuario {

    // --- Atributos de estado ---
    private Usuario usuarioActual;
    private List<Libro> libros = new ArrayList<>();
    // SE ELIMINÓ: private List<Integer> favoritos = new ArrayList<>();
    private int prestamosActivos = 0;

    // --- Elementos FXML de la VISTA CATÁLOGO ---
    @FXML private Label labelBienvenida;
    // SE ELIMINÓ: @FXML private Label labelContadorFavoritos;
    @FXML private Label labelContadorPrestamos;
    @FXML private FlowPane contenedorLibros;
    @FXML private TextField filtroTitulo;
    @FXML private TextField filtroAutor;
    @FXML private ChoiceBox<String> filtroGenero;
    // SE ELIMINÓ: @FXML private ToggleButton toggleFavoritos; // Asumimos que este era el ToggleButton de favoritos

    /**
     * Inicializa el controlador. Se usa para enlazar listeners a los filtros.
     */
    @FXML
    public void initialize() {
        // Enlazar listeners para que el filtrado se haga automáticamente
        if (filtroTitulo != null) filtroTitulo.textProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());
        if (filtroAutor != null) filtroAutor.textProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());
        if (filtroGenero != null) filtroGenero.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());

        // SE ELIMINÓ la lógica relacionada con toggleFavoritos
    }

    /**
     * Método principal para establecer el usuario y cargar los datos iniciales.
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;

        if (labelBienvenida != null) labelBienvenida.setText("Bienvenido, " + usuario.getNombre());

        // Cargar todos los datos desde la base de datos
        libros = obtenerTodosLosLibros();
        // SE ELIMINÓ la llamada a obtenerFavoritosUsuario()
        prestamosActivos = contarPrestamosActivos(usuario.getId());

        // Actualizar contadores de la vista
        // SE ELIMINÓ: if (labelContadorFavoritos != null) labelContadorFavoritos.setText(String.valueOf(favoritos.size()));
        if (labelContadorPrestamos != null) labelContadorPrestamos.setText(String.valueOf(prestamosActivos));

        // Mostrar la lista inicial de libros
        mostrarLibrosFiltrados();
    }


    // --- LÓGICA DE FILTRADO Y VISUALIZACIÓN ---

    /**
     * Aplica los filtros actuales y actualiza el FlowPane de libros.
     */
    @FXML
    public void mostrarLibrosFiltrados() {
        if (contenedorLibros == null) return;

        contenedorLibros.getChildren().clear();
        String titulo = filtroTitulo.getText().toLowerCase();
        String autor = filtroAutor.getText().toLowerCase();
        String genero = filtroGenero.getValue() != null && !filtroGenero.getValue().equals("Todas")
                ? filtroGenero.getValue().toLowerCase() : "";

        // SE ELIMINÓ: boolean soloFavoritos = toggleFavoritos.isSelected();

        for (Libro libro : libros) {
            boolean coincide = libro.getTitulo().toLowerCase().contains(titulo)
                    && libro.getAutor().toLowerCase().contains(autor)
                    && (genero.isEmpty() || libro.getGenero().toLowerCase().equals(genero));
            // SE ELIMINÓ: && (!soloFavoritos || favoritos.contains(libro.getId()));

            if (coincide) {
                contenedorLibros.getChildren().add(crearVistaLibroItem(libro));
            }
        }
    }

    // --- LÓGICA DE INTERACCIÓN DE TARJETAS (MÉTODOS PÚBLICOS) ---

    /**
     * Método llamado por el ControladorVistaLibroItem para alternar el favorito.
     * DEBE SER REEMPLAZADO o ELIMINADO de la vista del item si ya no gestiona favoritos.
     * Si se mantiene, DEBE llamar a la lógica de favoritos (DAO) que está fuera de este controlador.
     */
    // SE ELIMINÓ: public void clickFavorito(Libro libro) { alternarFavorito(libro.getId()); }
    // DEBEMOS ASUMIR QUE EL CONTROLADOR DEL ITEM LO MANEJA DE OTRA FORMA AHORA.

    /**
     * Método llamado por el ControladorVistaLibroItem para pedir un préstamo.
     */
    public void clickPedirPrestamo(Libro libro) {
        if (pedirPrestado(libro.getId())) { // Asume que esta función llama al DAO de Préstamos
            // Refrescar la vista y mostrar confirmación
            mostrarAlerta("Préstamo Exitoso", "Has solicitado el préstamo de " + libro.getTitulo() + ".");
            // Actualizar la disponibilidad en el catálogo
            libros = obtenerTodosLosLibros(); // Refresca los datos del catálogo
            mostrarLibrosFiltrados();
        } else {
            mostrarAlertaError("Límite de Préstamo", "No puedes solicitar más préstamos o el libro no está disponible.");
        }
    }

    /**
     * Método llamado por el ControladorVistaLibroItem para ver detalles.
     */
    public void clickVer(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Libro-Usuario.fxml"));
            Parent root = loader.load();

            // Asume que este controlador maneja la vista de detalle
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

            // ELIMINADA la parte de 'favoritos.contains(libro.getId())'
            // El controlador del item (ControladorLibroCatalogo) ya NO recibe el estado de favorito
            // ni tiene una referencia a 'this' (el controlador del catálogo) para gestionar favoritos.
            // Si el item debe seguir permitiendo marcar favoritos, debe llamar a un DAO directamente.

            // Si el setDatos original era: controlador.setDatos(Libro libro, boolean esFavorito, Object controladorPadre);
            // Ahora debe ser algo como:
            // controlador.setDatos(libro, usuarioActual); // Si el item gestiona el favorito internamente

            // Usaremos una firma simple para demostrar la separación
            // Nota: Debes actualizar la firma real en tu ControladorLibroCatalogo
            controlador.setLibro(libro);

            return item;
        } catch (IOException e) {
            e.printStackTrace();
            return new Label("Error al cargar item: " + libro.getTitulo());
        }
    }

    // --- LÓGICA DE BASE DE DATOS (Stub - Debe ir en DAO) ---

    private List<Libro> obtenerTodosLosLibros() {
        // Lógica real de la base de datos (SELECT * FROM LIBROS)
        return new ArrayList<>(); // Placeholder
    }


    private int contarPrestamosActivos(int idUsuario) {
        // Lógica real de la base de datos (COUNT de préstamos activos)
        return 0; // Placeholder
    }

    private boolean pedirPrestado(int idLibro) {
        // Lógica real de la base de datos (INSERT préstamo y UPDATE disponibilidad)
        return true; // Placeholder
    }


    // --- UTILIDADES ---
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
